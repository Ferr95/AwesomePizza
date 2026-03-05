package com.awesomepizza.repository;

import com.awesomepizza.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/** Repository for order items. Not used directly: items are managed in cascade by the Order. */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
