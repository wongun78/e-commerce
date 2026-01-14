package fpt.kiennt169.e_commerce.mappers;

import fpt.kiennt169.e_commerce.dtos.product.*;
import fpt.kiennt169.e_commerce.entities.Product;
import fpt.kiennt169.e_commerce.entities.ProductImage;
import fpt.kiennt169.e_commerce.entities.ProductVariant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "variants", source = "variants")
    @Mapping(target = "totalStock", source = "product", qualifiedByName = "getTotalStock")
    @Mapping(target = "inStock", source = "product", qualifiedByName = "isInStock")
    ProductDTO toDTO(Product product);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "totalStock", source = "product", qualifiedByName = "getTotalStock")
    @Mapping(target = "inStock", source = "product", qualifiedByName = "isInStock")
    ProductListDTO toListDTO(Product product);

    List<ProductListDTO> toListDTOs(List<Product> products);

    ProductImageDTO toImageDTO(ProductImage image);

    List<ProductImageDTO> toImageDTOs(List<ProductImage> images);

    @Mapping(target = "inStock", source = "variant", qualifiedByName = "variantInStock")
    ProductVariantDTO toVariantDTO(ProductVariant variant);

    List<ProductVariantDTO> toVariantDTOs(List<ProductVariant> variants);

    @Named("getTotalStock")
    default Integer getTotalStock(Product product) {
        return product.getTotalStock();
    }

    @Named("isInStock")
    default Boolean isInStock(Product product) {
        return product.isInStock();
    }

    @Named("variantInStock")
    default Boolean variantInStock(ProductVariant variant) {
        return variant.isInStock();
    }
}

