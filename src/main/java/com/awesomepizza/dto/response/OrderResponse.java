package com.awesomepizza.dto.response;

import com.awesomepizza.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Full response DTO for an order (used by the pizzaiolo).
 * Includes internal ID, customer details, and list of items with prices.
 */
public class OrderResponse {

    private UUID id;
    private String trackingCode;
    private String customerName;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
}
