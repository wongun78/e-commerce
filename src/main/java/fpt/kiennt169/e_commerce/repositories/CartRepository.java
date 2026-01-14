package fpt.kiennt169.e_commerce.repositories;

import fpt.kiennt169.e_commerce.entities.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    
    @EntityGraph(attributePaths = {"items", "items.productVariant", "items.productVariant.product"})
    Optional<Cart> findByUserId(Long userId);
    
    @EntityGraph(attributePaths = {"items", "items.productVariant", "items.productVariant.product"})
    Optional<Cart> findBySessionId(String sessionId);
    
    boolean existsByUserId(Long userId);
    
    boolean existsBySessionId(String sessionId);
    
    @Query("SELECT c FROM Cart c WHERE c.updatedAt < :cutoffTime AND c.user IS NULL")
    List<Cart> findAbandonedGuestCarts(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.updatedAt < :cutoffTime AND c.user IS NULL")
    int deleteAbandonedGuestCarts(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Modifying
    @Query("UPDATE Cart c SET c.user.id = :userId, c.sessionId = NULL WHERE c.sessionId = :sessionId")
    int mergeGuestCartToUser(@Param("sessionId") String sessionId, @Param("userId") Long userId);
}
