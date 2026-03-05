package com.awesomepizza.dto.response;

import com.awesomepizza.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Reduced response DTO for tracking (used by the customer).
 * Exposes only tracking code and status, hiding internal ID and item details.
 */
public class OrderTrackingResponse {

    private String trackingCode;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
