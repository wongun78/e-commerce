package fpt.kiennt169.e_commerce.controllers;

import fpt.kiennt169.e_commerce.dtos.order.OrderDTO;
import fpt.kiennt169.e_commerce.exceptions.BadRequestException;
import fpt.kiennt169.e_commerce.exceptions.ResourceNotFoundException;
import fpt.kiennt169.e_commerce.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PublicOrderViewController {

    private final OrderService orderService;

    @GetMapping("/api/v1/public/orders/{orderId}")
    public String trackOrder(
            @PathVariable String orderId,
            @RequestParam String email,
            Model model) {
        
        log.info("Public order tracking (HTML view): orderId={}, email={}", orderId, email);
        
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        
        OrderDTO order = orderService.getOrderById(orderId);
        
        if (!email.equalsIgnoreCase(order.getCustomerEmail())) {
            log.warn("Email mismatch for order tracking: orderId={}, provided={}", orderId, email);
            throw new ResourceNotFoundException("Order", "id and email", orderId);
        }
        
        model.addAttribute("order", order);
        log.info("Order tracking page rendered: orderId={}", orderId);
        
        return "order-tracking";
    }
}
