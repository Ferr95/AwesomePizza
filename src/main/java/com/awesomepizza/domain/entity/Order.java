package com.awesomepizza.domain.entity;

import com.awesomepizza.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a pizza order.
 * The table is named "pizza_order" to avoid conflicts with the SQL reserved word "order".
 *
 * Each order has:
 * - A unique 8-character tracking code (e.g. "A1B2C3D4") for client-side tracking
 * - A status that follows the state machine defined in {@link OrderStatus}
 * - A list of {@link OrderItem} (the ordered pizzas with their quantities)
 *
 * The factory method {@link #create(String)} handles creation with default values
 * (PENDING status, auto-generated tracking code, current timestamps).
 */
@Entity
@Table(name = "pizza_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    /** Unique 8-character code for client-side order tracking */
    @Column(name = "tracking_code", nullable = false, unique = true)
    private String trackingCode;

    /** Customer name (optional) */
    @Column(name = "customer_name")
    private String customerName;

    /** Current order status (PENDING -> IN_PROGRESS -> READY -> PICKED_UP) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** List of ordered pizzas. Cascade ALL: saving the order also saves the items */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Factory method to create a new order.
     * Automatically sets PENDING status, generates the tracking code, and sets timestamps.
     */
    public static Order create(String customerName) {
        LocalDateTime now = LocalDateTime.now();
        return Order.builder()
                .trackingCode(generateTrackingCode())
                .customerName(customerName)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .items(new ArrayList<>())
                .build();
    }

    /** Adds an item to the order and sets the bidirectional relationship */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /** Generates an 8-character uppercase alphanumeric tracking code */
    private static String generateTrackingCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
