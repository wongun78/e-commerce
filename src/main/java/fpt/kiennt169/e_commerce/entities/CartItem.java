package fpt.kiennt169.e_commerce.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items", indexes = {
        @Index(name = "idx_cart_items_cart", columnList = "cart_id"),
        @Index(name = "idx_cart_items_variant", columnList = "product_variant_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_variant", columnNames = {"cart_id", "product_variant_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public BigDecimal getSubtotal() {
        return productVariant.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getUnitPrice() {
        return productVariant.getPrice();
    }

    public boolean exceedsStock() {
        return quantity > productVariant.getStockQuantity();
    }
}
