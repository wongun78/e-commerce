package fpt.kiennt169.e_commerce.dtos.order;

import fpt.kiennt169.e_commerce.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {

    private String id;
    private String customerName;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Integer totalItems;
    private LocalDateTime createdAt;
}
