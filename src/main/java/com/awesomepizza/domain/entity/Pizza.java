package com.awesomepizza.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity representing a pizza on the menu.
 * Contains name, description, and price.
 * Pizzas are loaded at startup via {@link com.awesomepizza.config.DataInitializer}.
 */
@Entity
@Table(name = "pizza")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pizza extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;
}
