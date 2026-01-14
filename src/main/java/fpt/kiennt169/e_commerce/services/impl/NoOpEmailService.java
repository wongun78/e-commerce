package fpt.kiennt169.e_commerce.services.impl;

import fpt.kiennt169.e_commerce.entities.Order;
import fpt.kiennt169.e_commerce.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpEmailService implements EmailService {

    @Override
    public void sendOrderConfirmation(Order order) {
        log.info("Email sending disabled. Would have sent order confirmation to: {}", order.getCustomerEmail());
    }

    @Override
    public void sendOrderStatusUpdate(Order order, String oldStatus, String newStatus) {
        log.info("Email sending disabled. Would have sent status update to: {}", order.getCustomerEmail());
    }
}
