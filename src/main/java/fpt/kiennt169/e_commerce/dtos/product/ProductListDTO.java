package fpt.kiennt169.e_commerce.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDTO {

    private Long id;
    private String name;
    private BigDecimal basePrice;

    private String categoryName;

    private Integer totalStock;
    private Boolean inStock;
}
