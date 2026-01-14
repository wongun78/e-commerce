package fpt.kiennt169.e_commerce.services.impl;

import fpt.kiennt169.e_commerce.dtos.order.CreateOrderRequest;
import fpt.kiennt169.e_commerce.dtos.order.OrderDTO;
import fpt.kiennt169.e_commerce.dtos.order.OrderListDTO;
import fpt.kiennt169.e_commerce.dtos.order.UpdateOrderStatusRequest;
import fpt.kiennt169.e_commerce.entities.*;
import fpt.kiennt169.e_commerce.enums.OrderStatus;
import fpt.kiennt169.e_commerce.exceptions.BadRequestException;
import fpt.kiennt169.e_commerce.exceptions.ResourceNotFoundException;
import fpt.kiennt169.e_commerce.mappers.OrderMapper;
import fpt.kiennt169.e_commerce.repositories.*;
import fpt.kiennt169.e_commerce.services.InventoryService;
import fpt.kiennt169.e_commerce.services.OrderService;
import fpt.kiennt169.e_commerce.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryService inventoryService;
    private final EmailService emailService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, String sessionId, CreateOrderRequest request) {
        log.debug("Creating order for userId={}, sessionId={}", userId, sessionId);

        Cart cart;
        if (userId != null) {
            cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        } else {
            cart = cartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart", "sessionId", sessionId));
        }

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        }

        Order order = Order.builder()
                .user(user)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("COD".equals(request.getPaymentMethod()) ? "PENDING" : "PENDING")
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            BigDecimal price = variant.getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(variant)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(price)
                    .build();

            order.addItem(orderItem);
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(totalAmount);

        // Confirm stock reservation
        String reservationSessionId = request.getReservationId();
        if (reservationSessionId == null) {
            // Fallback: If no reservation, use session/user ID (backward compatible)
            reservationSessionId = sessionId != null ? sessionId : "user-" + userId;
        }
        
        // This will deduct stock and mark reservations as completed
        inventoryService.confirmStock(reservationSessionId);

        order = orderRepository.save(order);

        cart.clearItems();
        cartRepository.save(cart);

        // Send order confirmation email
        try {
            emailService.sendOrderConfirmation(order);
        } catch (Exception e) {
            log.warn("Failed to send order confirmation email for order: {}", order.getId(), e);
        }

        log.info("Order created: id={}", order.getId());
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(String orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Check if user owns this order
        if (userId != null && order.getUser() != null && !order.getUser().getId().equals(userId)) {
            throw new fpt.kiennt169.e_commerce.exceptions.ForbiddenException("You don't have permission to view this order");
        }
        
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(orderMapper::toListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(orderMapper::toListDTO);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        validateStatusTransition(order.getStatus(), request.getStatus());

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());
        order = orderRepository.save(order);

        log.info("Order status updated: id={}, status={}", orderId, request.getStatus());
        
        // Send status update email
        try {
            emailService.sendOrderStatusUpdate(order, oldStatus.name(), request.getStatus().name());
        } catch (Exception e) {
            log.warn("Failed to send status update email for order: {}", order.getId(), e);
        }
        
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.canCancel()) {
            throw new BadRequestException("Order cannot be cancelled. Current status: " + order.getStatus());
        }

        for (OrderItem item : order.getItems()) {
            ProductVariant variant = variantRepository.findByIdWithLock(item.getProductVariant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", item.getProductVariant().getId()));

            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
            variantRepository.save(variant);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        log.info("Order cancelled: id={}", orderId);
        return orderMapper.toDTO(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus target) {
        boolean valid = switch (current) {
            case PENDING -> target == OrderStatus.CONFIRMED || target == OrderStatus.CANCELLED;
            case CONFIRMED -> target == OrderStatus.SHIPPED || target == OrderStatus.CANCELLED;
            case SHIPPED -> target == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    String.format("Invalid status transition: %s -> %s", current, target));
        }
    }
}
