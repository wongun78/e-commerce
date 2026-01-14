package fpt.kiennt169.e_commerce.services;

import fpt.kiennt169.e_commerce.dtos.cart.*;

public interface CartService {

    /**
     * Get cart by user ID
     */
    CartDTO getCartByUserId(Long userId);

    /**
     * Get cart by session ID (for guest)
     */
    CartDTO getCartBySessionId(String sessionId);

    /**
     * Get or create cart for user
     */
    CartDTO getOrCreateCartForUser(Long userId);

    /**
     * Get or create cart for guest session
     */
    CartDTO getOrCreateCartForSession(String sessionId);

    /**
     * Add item to cart
     */
    CartDTO addToCart(Long userId, String sessionId, AddToCartRequest request);

    /**
     * Update cart item quantity
     */
    CartDTO updateCartItem(Long userId, String sessionId, String cartItemId, UpdateCartItemRequest request);

    /**
     * Remove item from cart
     */
    CartDTO removeFromCart(Long userId, String sessionId, String cartItemId);
}
