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
/** Response DTO for a pizza on the menu */
public class PizzaResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
}
