package fpt.kiennt169.e_commerce.services;

import fpt.kiennt169.e_commerce.entities.Order;

public interface EmailService {
    
    /**
     * Send order confirmation email
     */
    void sendOrderConfirmation(Order order);
    
    /**
     * Send order status update email
     */
    void sendOrderStatusUpdate(Order order, String oldStatus, String newStatus);
}
