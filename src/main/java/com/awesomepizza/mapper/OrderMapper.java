package com.awesomepizza.mapper;

import com.awesomepizza.domain.entity.Order;
import com.awesomepizza.domain.entity.OrderItem;
import com.awesomepizza.domain.entity.Pizza;
import com.awesomepizza.dto.response.OrderItemResponse;
import com.awesomepizza.dto.response.OrderResponse;
import com.awesomepizza.dto.response.OrderTrackingResponse;
import com.awesomepizza.dto.response.PizzaResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Manual mapper for converting JPA entities to response DTOs.
 * Centralizes all Entity -> DTO conversions in a single place.
 * Used by both OrderServiceImpl and PizzaServiceImpl.
 */
@Component
public class OrderMapper {

    /** Converts an Order to a full OrderResponse (for the pizzaiolo, includes all details) */
    public OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .trackingCode(order.getTrackingCode())
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream()
                        .map(this::toOrderItemResponse)
                        .toList())
                .build();
    }

    /** Converts an Order to a reduced OrderTrackingResponse (for the customer, only tracking code and status) */
    public OrderTrackingResponse toTrackingResponse(Order order) {
        return OrderTrackingResponse.builder()
                .trackingCode(order.getTrackingCode())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public PizzaResponse toPizzaResponse(Pizza pizza) {
        return PizzaResponse.builder()
                .id(pizza.getId())
                .name(pizza.getName())
                .description(pizza.getDescription())
                .price(pizza.getPrice())
                .build();
    }

    public List<OrderResponse> toOrderResponseList(List<Order> orders) {
        return orders.stream().map(this::toOrderResponse).toList();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .pizzaId(item.getPizza().getId())
                .pizzaName(item.getPizza().getName())
                .quantity(item.getQuantity())
                .price(item.getPizza().getPrice())
                .build();
    }
}
