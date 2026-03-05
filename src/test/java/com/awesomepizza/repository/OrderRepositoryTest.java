package com.awesomepizza.repository;

import com.awesomepizza.domain.entity.Order;
import com.awesomepizza.domain.entity.Pizza;
import com.awesomepizza.domain.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("OrderRepository")
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Pizza pizza;

    @BeforeEach
    void setUp() {
        pizza = Pizza.builder()
                .name("Margherita")
                .description("Classic")
                .price(new BigDecimal("8.50"))
                .build();
        entityManager.persist(pizza);
    }

    @Test
    @DisplayName("Should find order by tracking code")
    void findByTrackingCode() {
        Order order = Order.create("John");
        entityManager.persist(order);
        entityManager.flush();

        Optional<Order> found = orderRepository.findByTrackingCode(order.getTrackingCode());

        assertThat(found).isPresent();
        assertThat(found.get().getTrackingCode()).isEqualTo(order.getTrackingCode());
        assertThat(found.get().getCustomerName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return empty when tracking code not found")
    void findByTrackingCode_notFound() {
        Optional<Order> found = orderRepository.findByTrackingCode("NONEXIST");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find orders by status ordered by createdAt ASC")
    void findByStatusOrderByCreatedAtAsc() {
        Order first = createOrderWithStatus("First", OrderStatus.PENDING, LocalDateTime.now().minusMinutes(10));
        Order second = createOrderWithStatus("Second", OrderStatus.PENDING, LocalDateTime.now().minusMinutes(5));
        Order third = createOrderWithStatus("Third", OrderStatus.IN_PROGRESS, LocalDateTime.now());

        entityManager.persist(first);
        entityManager.persist(second);
        entityManager.persist(third);
        entityManager.flush();

        List<Order> pendingOrders = orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.PENDING);

        assertThat(pendingOrders).hasSize(2);
        assertThat(pendingOrders.get(0).getCustomerName()).isEqualTo("First");
        assertThat(pendingOrders.get(1).getCustomerName()).isEqualTo("Second");
    }

    @Test
    @DisplayName("Should find orders by multiple statuses ordered by createdAt ASC")
    void findByStatusInOrderByCreatedAtAsc() {
        Order pending = createOrderWithStatus("Pending", OrderStatus.PENDING, LocalDateTime.now().minusMinutes(10));
        Order inProgress = createOrderWithStatus("InProgress", OrderStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(5));
        Order pickedUp = createOrderWithStatus("PickedUp", OrderStatus.PICKED_UP, LocalDateTime.now());

        entityManager.persist(pending);
        entityManager.persist(inProgress);
        entityManager.persist(pickedUp);
        entityManager.flush();

        List<Order> activeOrders = orderRepository.findByStatusInOrderByCreatedAtAsc(
                List.of(OrderStatus.PENDING, OrderStatus.IN_PROGRESS));

        assertThat(activeOrders).hasSize(2);
        assertThat(activeOrders.get(0).getCustomerName()).isEqualTo("Pending");
        assertThat(activeOrders.get(1).getCustomerName()).isEqualTo("InProgress");
    }

    @Test
    @DisplayName("Should check if order with status exists")
    void existsByStatus() {
        Order order = Order.create("Test");
        order.setStatus(OrderStatus.IN_PROGRESS);
        entityManager.persist(order);
        entityManager.flush();

        assertThat(orderRepository.existsByStatus(OrderStatus.IN_PROGRESS)).isTrue();
        assertThat(orderRepository.existsByStatus(OrderStatus.READY)).isFalse();
    }

    @Test
    @DisplayName("Should find first order by status")
    void findFirstByStatus() {
        Order order = Order.create("Test");
        order.setStatus(OrderStatus.IN_PROGRESS);
        entityManager.persist(order);
        entityManager.flush();

        Optional<Order> found = orderRepository.findFirstByStatus(OrderStatus.IN_PROGRESS);

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("Test");
    }

    private Order createOrderWithStatus(String customerName, OrderStatus status, LocalDateTime createdAt) {
        return Order.builder()
                .trackingCode(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerName(customerName)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }
}
