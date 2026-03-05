package com.awesomepizza.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/** Response DTO for a single order line item (pizza name, quantity, price) */
public class OrderItemResponse {

    private UUID pizzaId;
    private String pizzaName;
    private Integer quantity;
    private BigDecimal price;
}
