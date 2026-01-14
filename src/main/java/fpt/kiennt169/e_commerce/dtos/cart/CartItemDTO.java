package fpt.kiennt169.e_commerce.dtos.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Long id;
    private Long variantId;
    private String variantSku;
    private String variantSize;
    private String variantColor;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    private Long productId;
    private String productName;
    private String productImageUrl;

    private Integer availableStock;
    private Boolean inStock;
    private Boolean exceedsStock;
}
