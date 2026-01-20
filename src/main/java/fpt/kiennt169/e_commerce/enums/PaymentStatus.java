package fpt.kiennt169.e_commerce.enums;

/**
 * Payment status enumeration
 * Tracks payment verification state
 */
public enum PaymentStatus {
    /**
     * Payment not yet received or verified
     * Default status for new orders
     */
    PENDING,

    /**
     * Payment received and verified
     * - COD: Confirmed by delivery driver
     * - BANK_TRANSFER: Verified by admin
     */
    PAID,

    /**
     * Payment failed or rejected
     * Requires customer action
     */
    FAILED,

    /**
     * Payment refunded to customer
     * Used for cancelled/returned orders
     */
    REFUNDED
}
