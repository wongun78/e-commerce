package fpt.kiennt169.e_commerce.repositories;

import fpt.kiennt169.e_commerce.entities.InventoryReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {
    
    @Modifying
    @Query("UPDATE InventoryReservation ir SET ir.status = 'CANCELLED' " +
           "WHERE ir.sessionId = :sessionId AND ir.status = 'ACTIVE'")
    int cancelReservationsBySessionId(@Param("sessionId") String sessionId);
    
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT COALESCE(SUM(ir.quantity), 0) FROM InventoryReservation ir " +
           "WHERE ir.productVariant.id = :variantId AND ir.status = 'ACTIVE'")
    int getTotalReservedQuantity(@Param("variantId") Long variantId);
    
    List<InventoryReservation> findBySessionIdAndStatus(String sessionId, String status);
    
    long countBySessionIdAndStatus(String sessionId, String status);
    
    @Query("SELECT ir FROM InventoryReservation ir " +
           "WHERE ir.expiryTime < :currentTime AND ir.status = 'ACTIVE'")
    List<InventoryReservation> findExpiredReservations(@Param("currentTime") LocalDateTime currentTime);
}
