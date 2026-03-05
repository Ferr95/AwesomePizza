package com.awesomepizza.controller;

import com.awesomepizza.dto.request.CreateOrderRequest;
import com.awesomepizza.dto.response.OrderTrackingResponse;
import com.awesomepizza.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for customer operations.
 * Allows placing a new order and tracking its status via tracking code.
 * The customer does not see internal details (ID, items), only the tracking code and status.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Customer Orders", description = "Place and track orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new pizza order")
    public ResponseEntity<OrderTrackingResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderTrackingResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{trackingCode}")
    @Operation(summary = "Track order status by tracking code")
    public ResponseEntity<OrderTrackingResponse> getOrderStatus(@PathVariable String trackingCode) {
        return ResponseEntity.ok(orderService.getOrderByTrackingCode(trackingCode));
    }
}
