package fpt.kiennt169.e_commerce.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    private Long id;
    private Long variantId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;
    
    private String productName;
    private String variantSku;
    private String variantSize;
    private String variantColor;
}
