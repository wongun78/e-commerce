package fpt.kiennt169.e_commerce.controllers;

import fpt.kiennt169.e_commerce.dtos.ApiResponse;
import fpt.kiennt169.e_commerce.dtos.cart.*;
import fpt.kiennt169.e_commerce.entities.User;
import fpt.kiennt169.e_commerce.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management APIs")
public class CartController {

    private final CartService cartService;
    private static final String GUEST_ID_HEADER = "X-Guest-ID";

    @GetMapping
    @Operation(summary = "Get cart", description = "Get current cart for user or guest")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @AuthenticationPrincipal User user,
            @RequestHeader(value = GUEST_ID_HEADER, required = false) String guestId) {

        Long userId = user != null ? user.getId() : null;
        String sessionId = user == null ? guestId : null;

        if (userId != null) {
            return ResponseEntity.ok(ApiResponse.success(cartService.getOrCreateCartForUser(userId)));
        } else if (sessionId != null) {
            return ResponseEntity.ok(ApiResponse.success(cartService.getOrCreateCartForSession(sessionId)));
        } else {
            throw new fpt.kiennt169.e_commerce.exceptions.BadRequestException("Either userId or guestId is required");
        }
    }

    @PostMapping("/items")
    @Operation(summary = "Add to cart", description = "Add item to cart")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @AuthenticationPrincipal User user,
            @RequestHeader(value = GUEST_ID_HEADER, required = false) String guestId,
            @Valid @RequestBody AddToCartRequest request) {

        Long userId = user != null ? user.getId() : null;
        String sessionId = user == null ? guestId : null;

        return ResponseEntity.ok(ApiResponse.success(cartService.addToCart(userId, sessionId, request)));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item", description = "Update item quantity in cart")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(
            @AuthenticationPrincipal User user,
            @RequestHeader(value = GUEST_ID_HEADER, required = false) String guestId,
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        Long userId = user != null ? user.getId() : null;
        String sessionId = user == null ? guestId : null;

        return ResponseEntity.ok(ApiResponse.success(cartService.updateCartItem(userId, sessionId, itemId, request)));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove from cart", description = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartDTO>> removeFromCart(
            @AuthenticationPrincipal User user,
            @RequestHeader(value = GUEST_ID_HEADER, required = false) String guestId,
            @PathVariable String itemId) {

        Long userId = user != null ? user.getId() : null;
        String sessionId = user == null ? guestId : null;

        return ResponseEntity.ok(ApiResponse.success(cartService.removeFromCart(userId, sessionId, itemId)));
    }
}
