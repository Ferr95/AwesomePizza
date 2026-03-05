package com.awesomepizza.repository;

import com.awesomepizza.domain.entity.Order;
import com.awesomepizza.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for orders.
 * Spring Data JPA automatically generates queries from method names.
 * "OrderByCreatedAtAsc" ensures FIFO ordering (oldest first).
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /** Finds an order by tracking code (used by the customer to track) */
    Optional<Order> findByTrackingCode(String trackingCode);

    /** Orders with a given status, sorted FIFO */
    List<Order> findByStatusOrderByCreatedAtAsc(OrderStatus status);

    /** Orders with one of the given statuses, sorted FIFO (used for the pizzaiolo's queue) */
    List<Order> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);

    /** Finds the first order with a given status (used to find the current IN_PROGRESS order) */
    Optional<Order> findFirstByStatus(OrderStatus status);

    /** Checks whether at least one order with a given status exists (used for the "one at a time" constraint) */
    boolean existsByStatus(OrderStatus status);
}
