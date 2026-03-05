package com.awesomepizza.service;

import com.awesomepizza.dto.request.CreateOrderRequest;
import com.awesomepizza.dto.request.UpdateOrderStatusRequest;
import com.awesomepizza.dto.response.OrderResponse;
import com.awesomepizza.dto.response.OrderTrackingResponse;

import java.util.List;
import java.util.UUID;

/**
 * Order service interface.
 * Handles creation, tracking, FIFO queue, and order status updates.
 */
public interface OrderService {

    /** Creates a new order and returns the tracking code to the customer */
    OrderTrackingResponse createOrder(CreateOrderRequest request);

    /** Finds an order by tracking code (used by the customer to track) */
    OrderTrackingResponse getOrderByTrackingCode(String trackingCode);

    /** Returns the queue of active orders (PENDING, IN_PROGRESS, READY) sorted FIFO */
    List<OrderResponse> getOrderQueue();

    /** Returns the order currently being prepared, or null if none */
    OrderResponse getCurrentOrder();

    /** Updates the status of an order (e.g. PENDING -> IN_PROGRESS) with transition validation */
    OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request);
}
