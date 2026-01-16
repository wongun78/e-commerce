package fpt.kiennt169.e_commerce.services.impl;

import fpt.kiennt169.e_commerce.dtos.cart.*;
import fpt.kiennt169.e_commerce.entities.Cart;
import fpt.kiennt169.e_commerce.entities.CartItem;
import fpt.kiennt169.e_commerce.entities.ProductVariant;
import fpt.kiennt169.e_commerce.entities.User;
import fpt.kiennt169.e_commerce.exceptions.BadRequestException;
import fpt.kiennt169.e_commerce.exceptions.InsufficientStockException;
import fpt.kiennt169.e_commerce.exceptions.ResourceNotFoundException;
import fpt.kiennt169.e_commerce.mappers.CartMapper;
import fpt.kiennt169.e_commerce.repositories.CartRepository;
import fpt.kiennt169.e_commerce.repositories.ProductVariantRepository;
import fpt.kiennt169.e_commerce.repositories.UserRepository;
import fpt.kiennt169.e_commerce.services.CartService;
import fpt.kiennt169.e_commerce.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public CartDTO getOrCreateCartForUser(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cartMapper::toDTO)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    cart = cartRepository.save(cart);
                    log.info("Created cart for user: {}", userId);
                    return cartMapper.toDTO(cart);
                });
    }

    @Override
    @Transactional
    public CartDTO getOrCreateCartForSession(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .map(cartMapper::toDTO)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .sessionId(sessionId)
                            .build();
                    cart = cartRepository.save(cart);
                    log.info("Created guest cart for session: {}", sessionId);
                    return cartMapper.toDTO(cart);
                });
    }

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, String sessionId, AddToCartRequest request) {
        log.debug("Adding to cart: variantId={}, quantity={}", request.getVariantId(), request.getQuantity());

        Cart cart = getOrCreateCart(userId, sessionId);

        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", request.getVariantId()));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(request.getVariantId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            int availableStock = inventoryService.getAvailableStock(request.getVariantId());
            if (availableStock < newQuantity) {
                throw new InsufficientStockException(
                    "Insufficient stock", newQuantity, availableStock);
            }

            item.setQuantity(newQuantity);
        } else {
            int availableStock = inventoryService.getAvailableStock(request.getVariantId());
            if (availableStock < request.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock", request.getQuantity(), availableStock);
            }

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(item);
        }

        cart = cartRepository.save(cart);

        log.info("Added to cart: variantId={}, quantity={}", request.getVariantId(), request.getQuantity());
        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long userId, String sessionId, String cartItemId, UpdateCartItemRequest request) {
        log.debug("Updating cart item: id={}, quantity={}", cartItemId, request.getQuantity());

        Cart cart = findCart(userId, sessionId);

        CartItem item = cart.getItems().stream()
                .filter(i -> cartItemId.equals(String.valueOf(i.getId())))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        ProductVariant variant = item.getProductVariant();
        int availableStock = inventoryService.getAvailableStock(variant.getId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientStockException(
                "Insufficient stock", request.getQuantity(), availableStock);
        }

        item.setQuantity(request.getQuantity());

        cart = cartRepository.save(cart);

        log.info("Updated cart item: id={}, quantity={}", cartItemId, request.getQuantity());
        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeFromCart(Long userId, String sessionId, String cartItemId) {
        log.debug("Removing cart item: id={}", cartItemId);

        Cart cart = findCart(userId, sessionId);

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> cartItemId.equals(String.valueOf(item.getId())))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        cart.removeItem(itemToRemove);

        cart = cartRepository.save(cart);

        log.info("Removed cart item: id={}", cartItemId);
        return cartMapper.toDTO(cart);
    }

    private Cart getOrCreateCart(Long userId, String sessionId) {
        if (userId != null) {
            return cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                        return Cart.builder()
                                .user(user)
                                .build();
                    });
        } else if (sessionId != null) {
            return cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> Cart.builder()
                            .sessionId(sessionId)
                            .build());
        } else {
            throw new BadRequestException("Either userId or sessionId must be provided");
        }
    }

    private Cart findCart(Long userId, String sessionId) {
        if (userId != null) {
            return cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        } else if (sessionId != null) {
            return cartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart", "sessionId", sessionId));
        } else {
            throw new BadRequestException("Either userId or sessionId must be provided");
        }
    }
}
