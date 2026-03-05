package com.awesomepizza.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * DTO for creating a new order request.
 * The customerName is optional, but there must be at least one item.
 * Each item is validated in cascade thanks to @Valid.
 */
public class CreateOrderRequest {

    private String customerName;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
