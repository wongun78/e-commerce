package fpt.kiennt169.e_commerce.mappers;

import fpt.kiennt169.e_commerce.dtos.order.OrderDTO;
import fpt.kiennt169.e_commerce.dtos.order.OrderItemDTO;
import fpt.kiennt169.e_commerce.dtos.order.OrderListDTO;
import fpt.kiennt169.e_commerce.entities.Order;
import fpt.kiennt169.e_commerce.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalItems", source = "order", qualifiedByName = "getTotalItems")
    OrderDTO toDTO(Order order);

    @Mapping(target = "totalItems", source = "order", qualifiedByName = "getTotalItems")
    OrderListDTO toListDTO(Order order);

    List<OrderListDTO> toListDTOs(List<Order> orders);

    @Mapping(target = "variantId", source = "productVariant.id")
    @Mapping(target = "subtotal", source = "item", qualifiedByName = "getSubtotal")
    @Mapping(target = "productName", source = "productVariant.product.name")
    @Mapping(target = "variantSku", source = "productVariant.sku")
    @Mapping(target = "variantSize", source = "productVariant.size")
    @Mapping(target = "variantColor", source = "productVariant.color")
    OrderItemDTO toItemDTO(OrderItem item);

    List<OrderItemDTO> toItemDTOs(List<OrderItem> items);

    @Named("getTotalItems")
    default Integer getTotalItems(Order order) {
        return order.getTotalItems();
    }

    @Named("getSubtotal")
    default BigDecimal getSubtotal(OrderItem item) {
        return item.getSubtotal();
    }
}
