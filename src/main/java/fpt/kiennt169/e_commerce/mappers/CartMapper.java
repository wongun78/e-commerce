package fpt.kiennt169.e_commerce.mappers;

import fpt.kiennt169.e_commerce.dtos.cart.CartDTO;
import fpt.kiennt169.e_commerce.dtos.cart.CartItemDTO;
import fpt.kiennt169.e_commerce.entities.Cart;
import fpt.kiennt169.e_commerce.entities.CartItem;
import fpt.kiennt169.e_commerce.entities.ProductImage;
import fpt.kiennt169.e_commerce.entities.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalItems", source = "cart", qualifiedByName = "getTotalItems")
    @Mapping(target = "totalPrice", source = "cart", qualifiedByName = "getTotalPrice")
    CartDTO toDTO(Cart cart);

    @Mapping(target = "variantId", source = "productVariant.id")
    @Mapping(target = "variantSku", source = "productVariant.sku")
    @Mapping(target = "variantSize", source = "productVariant.size")
    @Mapping(target = "variantColor", source = "productVariant.color")
    @Mapping(target = "unitPrice", source = "item", qualifiedByName = "getUnitPrice")
    @Mapping(target = "subtotal", source = "item", qualifiedByName = "getSubtotal")
    @Mapping(target = "productId", source = "productVariant.product.id")
    @Mapping(target = "productName", source = "productVariant.product.name")
    @Mapping(target = "productImageUrl", source = "item", qualifiedByName = "getProductImageUrl")
    @Mapping(target = "availableStock", source = "productVariant.stockQuantity")
    @Mapping(target = "inStock", source = "productVariant", qualifiedByName = "isVariantInStock")
    @Mapping(target = "exceedsStock", source = "item", qualifiedByName = "doesExceedStock")
    CartItemDTO toItemDTO(CartItem item);

    List<CartItemDTO> toItemDTOs(List<CartItem> items);

    @Named("getTotalItems")
    default Integer getTotalItems(Cart cart) {
        if (cart.getItems() == null) return 0;
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Named("getTotalPrice")
    default BigDecimal getTotalPrice(Cart cart) {
        if (cart.getItems() == null) return BigDecimal.ZERO;
        return cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Named("getUnitPrice")
    default BigDecimal getUnitPrice(CartItem item) {
        return item.getUnitPrice();
    }

    @Named("getSubtotal")
    default BigDecimal getSubtotal(CartItem item) {
        return item.getSubtotal();
    }

    @Named("getProductImageUrl")
    default String getProductImageUrl(CartItem item) {
        if (item.getProductVariant() == null || item.getProductVariant().getProduct() == null) {
            return null;
        }
        List<ProductImage> images = item.getProductVariant().getProduct().getImages();
        return (images != null && !images.isEmpty()) ? images.get(0).getImageUrl() : null;
    }

    @Named("isVariantInStock")
    default Boolean isVariantInStock(ProductVariant variant) {
        return variant != null && variant.isInStock();
    }

    @Named("doesExceedStock")
    default Boolean doesExceedStock(CartItem item) {
        return item.exceedsStock();
    }
}
