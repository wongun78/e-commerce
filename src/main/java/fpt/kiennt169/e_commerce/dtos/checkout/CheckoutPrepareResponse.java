package fpt.kiennt169.e_commerce.dtos.checkout;

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
public class CheckoutPrepareResponse {
    private String reservationId;  
    private LocalDateTime expiresAt; 
    private List<ReservedItem> items;
    private BigDecimal totalAmount;
    private String message;  

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservedItem {
        private Long variantId;
        private String productName;
        private String variantDetails;  
        private int quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }
}
