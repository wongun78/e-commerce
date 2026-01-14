package fpt.kiennt169.e_commerce.services;

import fpt.kiennt169.e_commerce.dtos.checkout.CheckoutPrepareResponse;

public interface CheckoutService {
    
    /**
     * Prepare checkout - Reserve stock for items
     */
    CheckoutPrepareResponse prepareCheckout(Long userId, String sessionId);
    
    /**
     * Verify reservation is still valid
     */
    boolean verifyReservation(String reservationSessionId);
}
