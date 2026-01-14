package fpt.kiennt169.e_commerce.controllers;

import fpt.kiennt169.e_commerce.dtos.ApiResponse;
import fpt.kiennt169.e_commerce.dtos.order.OrderDTO;
import fpt.kiennt169.e_commerce.exceptions.BadRequestException;
import fpt.kiennt169.e_commerce.exceptions.ResourceNotFoundException;
import fpt.kiennt169.e_commerce.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Order Tracking", description = "APIs for tracking orders without authentication")
public class PublicOrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Track order", description = "Track order status using order ID and email (no authentication required)")
    public ResponseEntity<ApiResponse<OrderDTO>> trackOrder(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            @Parameter(description = "Customer email") @RequestParam String email) {
        
        log.info("Public order tracking request: orderId={}, email={}", orderId, email);
        
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        
        OrderDTO order = orderService.getOrderById(orderId);
        
        if (!email.equalsIgnoreCase(order.getCustomerEmail())) {
            log.warn("Email mismatch for order tracking: orderId={}, provided={}", orderId, email);
            throw new ResourceNotFoundException("Order", "id and email", orderId);
        }
        
        log.info("Order tracked successfully: orderId={}", orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
