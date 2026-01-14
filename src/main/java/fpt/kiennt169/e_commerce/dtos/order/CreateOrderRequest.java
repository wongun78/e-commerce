package fpt.kiennt169.e_commerce.dtos.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String customerEmail;

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    @Pattern(regexp = "^(\\+84|0)[0-9]{9,10}$", message = "Invalid phone number format")
    private String customerPhone;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(COD|BANK_TRANSFER)$", message = "Payment method must be COD or BANK_TRANSFER")
    private String paymentMethod;

    private String reservationId; // Optional: if checkout/prepare was called first

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

}
