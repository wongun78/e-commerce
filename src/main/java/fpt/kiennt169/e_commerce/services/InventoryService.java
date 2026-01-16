package fpt.kiennt169.e_commerce.services;

public interface InventoryService {

    /**
     * Reserve stock for a variant
     */
    void reserveStock(Long variantId, int quantity, String sessionId);

    /**
     * Release stock reservation
     */
    void releaseStock(Long variantId, int quantity, String sessionId);

    /**
     * Confirm stock (deduct from inventory after order placed)
     */
    void confirmStock(String sessionId);

    /**
     * Get available stock for a variant
     */
    int getAvailableStock(Long variantId);

    /**
     * Clean up expired reservations
     */
    void cleanupExpiredReservations();
}
