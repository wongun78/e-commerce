package fpt.kiennt169.e_commerce.controllers;

import fpt.kiennt169.e_commerce.dtos.ApiResponse;
import fpt.kiennt169.e_commerce.dtos.order.*;
import fpt.kiennt169.e_commerce.entities.User;
import fpt.kiennt169.e_commerce.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;
    private static final String GUEST_ID_HEADER = "X-Guest-ID";

    @PostMapping
    @Operation(summary = "Create order", description = "Create order from cart (checkout)")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @AuthenticationPrincipal User user,
            @RequestHeader(value = GUEST_ID_HEADER, required = false) String guestId,
            @Valid @RequestBody CreateOrderRequest request) {

        Long userId = user != null ? user.getId() : null;
        String sessionId = user == null ? guestId : null;

        return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(userId, sessionId, request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Get order details by ID (own order or with access)")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @AuthenticationPrincipal User user,
            @PathVariable String id) {
        Long userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id, userId)));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my orders", description = "Get paginated list of authenticated user's orders")
    public ResponseEntity<ApiResponse<Page<OrderListDTO>>> getMyOrders(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        
        if (user == null) {
            throw new fpt.kiennt169.e_commerce.exceptions.UnauthorizedException("Authentication required");
        }
        
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(user.getId(), pageable)));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all orders", description = "Admin only: Get paginated list of all orders")
    public ResponseEntity<ApiResponse<Page<OrderListDTO>>> getAllOrdersAdmin(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(pageable)));
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get order by ID (Admin)", description = "Admin only: Get full order details")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByIdAdmin(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id)));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update order status", description = "Admin only: Update order status (PENDING -> SHIPPING -> DELIVERED or CANCELLED)")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderService.updateOrderStatus(id, request)));
    }
}
