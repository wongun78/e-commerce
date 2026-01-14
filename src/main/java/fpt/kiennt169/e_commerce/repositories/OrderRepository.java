package fpt.kiennt169.e_commerce.repositories;

import fpt.kiennt169.e_commerce.entities.Order;
import fpt.kiennt169.e_commerce.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    @EntityGraph(attributePaths = {"items", "items.productVariant"})
    Optional<Order> findById(String id);
    
    @EntityGraph(attributePaths = {"items"})
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"items"})
    Page<Order> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE " +
           "LOWER(o.id) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Order> searchOrders(@Param("keyword") String keyword, Pageable pageable);
    
    Page<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable);
    
    long countByStatus(OrderStatus status);
    
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.status != 'CANCELLED' " +
           "AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenue(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    List<Order> findTop10ByOrderByCreatedAtDesc();
    
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startOfDay")
    long countOrdersToday(@Param("startOfDay") LocalDateTime startOfDay);
}
