package fpt.kiennt169.e_commerce.enums;

/**
 * Inventory reservation status enumeration
 * Lifecycle: ACTIVE (15 min) → COMPLETED (order created) or EXPIRED (timeout)
 */
public enum ReservationStatus {
    /**
     * Reservation is active and stock is held
     * Expires after 15 minutes if not confirmed
     */
    ACTIVE,

    /**
     * Reservation was confirmed and order created
     * Stock has been deducted from inventory
     */
    COMPLETED,

    /**
     * Reservation expired without order creation
     * Stock has been released back to inventory
     */
    EXPIRED,

    /**
     * Reservation was manually cancelled
     * Stock has been released back to inventory
     */
    CANCELLED
}
