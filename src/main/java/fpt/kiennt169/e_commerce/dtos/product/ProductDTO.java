package fpt.kiennt169.e_commerce.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private BigDecimal basePrice;
    private Boolean isActive;

    private Long categoryId;
    private String categoryName;

    private List<ProductImageDTO> images;

    private List<ProductVariantDTO> variants;
    private Integer totalStock;
    private Boolean inStock;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
