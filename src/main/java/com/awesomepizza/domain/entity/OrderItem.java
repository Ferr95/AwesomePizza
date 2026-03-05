package com.awesomepizza.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a single line item of an order.
 * Links an {@link Order} to a {@link Pizza} with a quantity.
 * Example: "2x Margherita" is an OrderItem with pizza=Margherita and quantity=2.
 *
 * OrderItems are managed in cascade by the parent order (no direct operations needed).
 */
@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    /** The order this item belongs to (LAZY to avoid unnecessary queries) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** The selected pizza */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pizza_id", nullable = false)
    private Pizza pizza;

    /** Quantity of this pizza in the order (minimum 1) */
    @Column(nullable = false)
    private Integer quantity;
}
