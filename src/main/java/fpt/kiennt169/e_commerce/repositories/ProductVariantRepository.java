package fpt.kiennt169.e_commerce.repositories;

import fpt.kiennt169.e_commerce.entities.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    Optional<ProductVariant> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    List<ProductVariant> findByProductId(Long productId);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity < 10")
    List<ProductVariant> findLowStockVariants();
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity = 0")
    List<ProductVariant> findOutOfStockVariants();
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.id = :id")
    Optional<ProductVariant> findByIdWithLock(@Param("id") Long id);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.sku = :sku")
    Optional<ProductVariant> findBySkuWithLock(@Param("sku") String sku);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.id IN :ids ORDER BY pv.id")
    List<ProductVariant> findAllByIdWithLock(@Param("ids") List<Long> ids);
    
    @Query("SELECT CASE WHEN pv.stockQuantity >= :quantity THEN true ELSE false END " +
           "FROM ProductVariant pv WHERE pv.id = :variantId")
    boolean hasEnoughStock(@Param("variantId") Long variantId, @Param("quantity") int quantity);
}
