package fpt.kiennt169.e_commerce.services.impl;

import fpt.kiennt169.e_commerce.dtos.checkout.CheckoutPrepareResponse;
import fpt.kiennt169.e_commerce.entities.Cart;
import fpt.kiennt169.e_commerce.entities.CartItem;
import fpt.kiennt169.e_commerce.entities.ProductVariant;
import fpt.kiennt169.e_commerce.exceptions.BadRequestException;
import fpt.kiennt169.e_commerce.repositories.CartRepository;
import fpt.kiennt169.e_commerce.repositories.InventoryReservationRepository;
import fpt.kiennt169.e_commerce.services.CheckoutService;
import fpt.kiennt169.e_commerce.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final InventoryService inventoryService;
    private final InventoryReservationRepository reservationRepository;
    
    @Value("${inventory.reservation.expiry-minutes:15}")
    private int reservationExpiryMinutes;

    @Override
    @Transactional
    public CheckoutPrepareResponse prepareCheckout(Long userId, String sessionId) {
        log.debug("Preparing checkout for userId={}, sessionId={}", userId, sessionId);
        
        // Get cart
        Cart cart;
        if (userId != null) {
            cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new BadRequestException("Cart not found"));
        } else {
            cart = cartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new BadRequestException("Cart not found"));
        }
        
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }
        
        // Generate reservation session ID
        String reservationSessionId = sessionId != null ? sessionId : "user-" + userId;
        
        // Reserve stock for each item
        List<CheckoutPrepareResponse.ReservedItem> reservedItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            
            // Reserve stock (this will throw exception if insufficient)
            inventoryService.reserveStock(
                    variant.getId(), 
                    cartItem.getQuantity(), 
                    reservationSessionId
            );
            
            // Build response item
            CheckoutPrepareResponse.ReservedItem reservedItem = CheckoutPrepareResponse.ReservedItem.builder()
                    .variantId(variant.getId())
                    .productName(variant.getProduct().getName())
                    .variantDetails(String.format("Size: %s, Color: %s", variant.getSize(), variant.getColor()))
                    .quantity(cartItem.getQuantity())
                    .price(variant.getPrice())
                    .subtotal(variant.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();
            
            reservedItems.add(reservedItem);
            totalAmount = totalAmount.add(reservedItem.getSubtotal());
        }
        
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(reservationExpiryMinutes);
        
        log.info("Checkout prepared: reservationId={}, items={}, expiresAt={}", 
                reservationSessionId, reservedItems.size(), expiresAt);
        
        return CheckoutPrepareResponse.builder()
                .reservationId(reservationSessionId)
                .expiresAt(expiresAt)
                .items(reservedItems)
                .totalAmount(totalAmount)
                .message(String.format("Stock reserved for %d minutes. Please complete your order.", reservationExpiryMinutes))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyReservation(String reservationSessionId) {
        log.debug("Verifying reservation: sessionId={}", reservationSessionId);
        
        long activeReservations = reservationRepository
                .countBySessionIdAndStatus(reservationSessionId, "ACTIVE");
        
        boolean isValid = activeReservations > 0;
        log.debug("Reservation valid: {}", isValid);
        
        return isValid;
    }
}
