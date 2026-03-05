package com.awesomepizza.controller;

import com.awesomepizza.dto.request.UpdateOrderStatusRequest;
import com.awesomepizza.dto.response.OrderResponse;
import com.awesomepizza.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for the pizzaiolo.
 * Allows viewing the order queue (FIFO), seeing the order currently being prepared,
 * and updating order status (PENDING -> IN_PROGRESS -> READY -> PICKED_UP).
 * Constraint: only one order can be IN_PROGRESS at a time.
 */
@RestController
@RequestMapping("/api/pizzaiolo/orders")
@RequiredArgsConstructor
@Tag(name = "Pizzaiolo", description = "Manage order queue and preparation")
public class PizzaioloController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get order queue (FIFO)")
    public ResponseEntity<List<OrderResponse>> getOrderQueue() {
        return ResponseEntity.ok(orderService.getOrderQueue());
    }

    /** Returns the order currently being prepared, or 204 No Content if the queue is free */
    @GetMapping("/current")
    @Operation(summary = "Get currently in-progress order")
    public ResponseEntity<OrderResponse> getCurrentOrder() {
        OrderResponse current = orderService.getCurrentOrder();
        if (current == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(current);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }
}
