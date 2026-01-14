package fpt.kiennt169.e_commerce.controllers;

import fpt.kiennt169.e_commerce.dtos.ApiResponse;
import fpt.kiennt169.e_commerce.dtos.checkout.CheckoutPrepareResponse;
import fpt.kiennt169.e_commerce.entities.User;
import fpt.kiennt169.e_commerce.services.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Checkout and inventory reservation APIs")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private static final String GUEST_ID_HEADER = "X-Guest-ID";

    @PostMapping("/prepare")
    @Operation(
        summary = "Prepare checkout", 
        description = "Reserve stock for cart items (15 minutes). Must be called before creating order."
    )
    public ResponseEntity<ApiResponse<CheckoutPrepareResponse>> prepareCheckout(
            @AuthenticationPrincipal User user,
            @RequestHeader(value = GUEST_ID_HEADER, required = false) String guestId) {

        Long userId = user != null ? user.getId() : null;
        String sessionId = user == null ? guestId : null;

        CheckoutPrepareResponse response = checkoutService.prepareCheckout(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/verify/{reservationId}")
    @Operation(
        summary = "Verify reservation", 
        description = "Check if reservation is still valid"
    )
    public ResponseEntity<ApiResponse<Boolean>> verifyReservation(@PathVariable String reservationId) {
        boolean isValid = checkoutService.verifyReservation(reservationId);
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }
}
