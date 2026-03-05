package com.awesomepizza.service.impl;

import com.awesomepizza.domain.entity.Order;
import com.awesomepizza.domain.entity.OrderItem;
import com.awesomepizza.domain.entity.Pizza;
import com.awesomepizza.domain.enums.OrderStatus;
import com.awesomepizza.dto.request.CreateOrderRequest;
import com.awesomepizza.dto.request.OrderItemRequest;
import com.awesomepizza.dto.request.UpdateOrderStatusRequest;
import com.awesomepizza.dto.response.OrderResponse;
import com.awesomepizza.dto.response.OrderTrackingResponse;
import com.awesomepizza.exception.InvalidStatusTransitionException;
import com.awesomepizza.exception.OrderAlreadyInProgressException;
import com.awesomepizza.exception.OrderNotFoundException;
import com.awesomepizza.exception.PizzaNotFoundException;
import com.awesomepizza.mapper.OrderMapper;
import com.awesomepizza.repository.OrderRepository;
import com.awesomepizza.repository.PizzaRepository;
import com.awesomepizza.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Order service implementation.
 * Contains all business logic: order creation, FIFO queue management,
 * status transition validation, and the "only one order in progress at a time" constraint.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PizzaRepository pizzaRepository;
    private final OrderMapper orderMapper;

    /**
     * Creates a new order:
     * 1. Generates the order with tracking code and PENDING status
     * 2. Verifies that each requested pizza exists on the menu
     * 3. Saves the order (items are saved in cascade)
     */
    @Override
    public OrderTrackingResponse createOrder(CreateOrderRequest request) {
        Order order = Order.create(request.getCustomerName());

        for (OrderItemRequest itemRequest : request.getItems()) {
            Pizza pizza = pizzaRepository.findById(itemRequest.getPizzaId())
                    .orElseThrow(() -> new PizzaNotFoundException(itemRequest.getPizzaId()));

            OrderItem item = OrderItem.builder()
                    .pizza(pizza)
                    .quantity(itemRequest.getQuantity())
                    .build();

            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toTrackingResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderTrackingResponse getOrderByTrackingCode(String trackingCode) {
        Order order = orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new OrderNotFoundException(trackingCode));
        return orderMapper.toTrackingResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderQueue() {
        List<OrderStatus> activeStatuses = List.of(OrderStatus.PENDING, OrderStatus.IN_PROGRESS, OrderStatus.READY);
        List<Order> orders = orderRepository.findByStatusInOrderByCreatedAtAsc(activeStatuses);
        return orderMapper.toOrderResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getCurrentOrder() {
        return orderRepository.findFirstByStatus(OrderStatus.IN_PROGRESS)
                .map(orderMapper::toOrderResponse)
                .orElse(null);
    }

    /**
     * Updates the status of an order with three levels of validation:
     * 1. The order must exist
     * 2. The status transition must be valid (e.g. cannot skip from PENDING to READY)
     * 3. If transitioning to IN_PROGRESS, there must not already be another order in progress
     */
    @Override
    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId.toString()));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        if (newStatus == OrderStatus.IN_PROGRESS && orderRepository.existsByStatus(OrderStatus.IN_PROGRESS)) {
            throw new OrderAlreadyInProgressException();
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(updatedOrder);
    }
}
