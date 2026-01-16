package fpt.kiennt169.e_commerce.services;

import fpt.kiennt169.e_commerce.dtos.order.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    /**
     * Create order from cart
     */
    OrderDTO createOrder(Long userId, String sessionId, CreateOrderRequest request);

    /**
     * Get order by ID
     */
    OrderDTO getOrderById(String orderId);

    /**
     * Get order by ID with user authorization
     */
    OrderDTO getOrderById(String orderId, Long userId);

    /**
     * Get orders by user ID (my orders)
     */
    Page<OrderListDTO> getUserOrders(Long userId, Pageable pageable);

    /**
     * Get all orders (admin)
     */
    Page<OrderListDTO> getAllOrders(Pageable pageable);

    /**
     * Update order status
     */
    OrderDTO updateOrderStatus(String orderId, UpdateOrderStatusRequest request);
}
