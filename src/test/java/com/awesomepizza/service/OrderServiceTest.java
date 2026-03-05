package com.awesomepizza.service;

import com.awesomepizza.domain.entity.Order;
import com.awesomepizza.domain.entity.OrderItem;
import com.awesomepizza.domain.entity.Pizza;
import com.awesomepizza.domain.enums.OrderStatus;
import com.awesomepizza.dto.request.CreateOrderRequest;
import com.awesomepizza.dto.request.OrderItemRequest;
import com.awesomepizza.dto.request.UpdateOrderStatusRequest;
import com.awesomepizza.dto.response.OrderResponse;
import com.awesomepizza.dto.response.OrderTrackingResponse;
import com.awesomepizza.exception.InvalidStatusTransitionException;
import com.awesomepizza.exception.OrderAlreadyInProgressException;
import com.awesomepizza.exception.OrderNotFoundException;
import com.awesomepizza.exception.PizzaNotFoundException;
import com.awesomepizza.mapper.OrderMapper;
import com.awesomepizza.repository.OrderRepository;
import com.awesomepizza.repository.PizzaRepository;
import com.awesomepizza.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Nested
    @DisplayName("createOrder")
    class CreateOrder {

        @Test
        @DisplayName("Should create order with valid request")
        void createOrder_success() {
            UUID pizzaId = UUID.randomUUID();
            Pizza pizza = Pizza.builder().name("Margherita").price(new BigDecimal("8.50")).build();
            pizza.setId(pizzaId);

            CreateOrderRequest request = CreateOrderRequest.builder()
                    .customerName("John")
                    .items(List.of(OrderItemRequest.builder().pizzaId(pizzaId).quantity(2).build()))
                    .build();

            OrderTrackingResponse expectedResponse = OrderTrackingResponse.builder()
                    .trackingCode("ABC12345")
                    .status(OrderStatus.PENDING)
                    .build();

            when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(pizza));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(orderMapper.toTrackingResponse(any(Order.class))).thenReturn(expectedResponse);

            OrderTrackingResponse result = orderService.createOrder(request);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should throw PizzaNotFoundException for invalid pizza ID")
        void createOrder_pizzaNotFound() {
            UUID invalidPizzaId = UUID.randomUUID();
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .customerName("John")
                    .items(List.of(OrderItemRequest.builder().pizzaId(invalidPizzaId).quantity(1).build()))
                    .build();

            when(pizzaRepository.findById(invalidPizzaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(PizzaNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getOrderByTrackingCode")
    class GetOrderByTrackingCode {

        @Test
        @DisplayName("Should return order tracking response for valid tracking code")
        void getOrder_success() {
            String trackingCode = "ABC12345";
            Order order = Order.builder()
                    .trackingCode(trackingCode)
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            OrderTrackingResponse expected = OrderTrackingResponse.builder()
                    .trackingCode(trackingCode)
                    .status(OrderStatus.PENDING)
                    .build();

            when(orderRepository.findByTrackingCode(trackingCode)).thenReturn(Optional.of(order));
            when(orderMapper.toTrackingResponse(order)).thenReturn(expected);

            OrderTrackingResponse result = orderService.getOrderByTrackingCode(trackingCode);

            assertThat(result.getTrackingCode()).isEqualTo(trackingCode);
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException for invalid tracking code")
        void getOrder_notFound() {
            when(orderRepository.findByTrackingCode("INVALID")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrderByTrackingCode("INVALID"))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateOrderStatus")
    class UpdateOrderStatus {

        @Test
        @DisplayName("Should update status from PENDING to IN_PROGRESS")
        void updateStatus_pendingToInProgress() {
            UUID orderId = UUID.randomUUID();
            Order order = createOrderWithStatus(orderId, OrderStatus.PENDING);
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.IN_PROGRESS).build();

            OrderResponse expected = OrderResponse.builder()
                    .id(orderId).status(OrderStatus.IN_PROGRESS).build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.existsByStatus(OrderStatus.IN_PROGRESS)).thenReturn(false);
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(expected);

            OrderResponse result = orderService.updateOrderStatus(orderId, request);

            assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should update status from IN_PROGRESS to READY")
        void updateStatus_inProgressToReady() {
            UUID orderId = UUID.randomUUID();
            Order order = createOrderWithStatus(orderId, OrderStatus.IN_PROGRESS);
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.READY).build();

            OrderResponse expected = OrderResponse.builder()
                    .id(orderId).status(OrderStatus.READY).build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(expected);

            OrderResponse result = orderService.updateOrderStatus(orderId, request);

            assertThat(result.getStatus()).isEqualTo(OrderStatus.READY);
        }

        @Test
        @DisplayName("Should throw InvalidStatusTransitionException for invalid transition")
        void updateStatus_invalidTransition() {
            UUID orderId = UUID.randomUUID();
            Order order = createOrderWithStatus(orderId, OrderStatus.PENDING);
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.READY).build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request))
                    .isInstanceOf(InvalidStatusTransitionException.class);
        }

        @Test
        @DisplayName("Should throw OrderAlreadyInProgressException when another order is in progress")
        void updateStatus_anotherOrderInProgress() {
            UUID orderId = UUID.randomUUID();
            Order order = createOrderWithStatus(orderId, OrderStatus.PENDING);
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.IN_PROGRESS).build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.existsByStatus(OrderStatus.IN_PROGRESS)).thenReturn(true);

            assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request))
                    .isInstanceOf(OrderAlreadyInProgressException.class);
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException for non-existent order")
        void updateStatus_orderNotFound() {
            UUID orderId = UUID.randomUUID();
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                    .status(OrderStatus.IN_PROGRESS).build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getOrderQueue")
    class GetOrderQueue {

        @Test
        @DisplayName("Should return active orders sorted by creation time")
        void getOrderQueue_success() {
            List<Order> orders = List.of(
                    createOrderWithStatus(UUID.randomUUID(), OrderStatus.PENDING),
                    createOrderWithStatus(UUID.randomUUID(), OrderStatus.IN_PROGRESS)
            );
            List<OrderResponse> expectedResponses = List.of(
                    OrderResponse.builder().status(OrderStatus.PENDING).build(),
                    OrderResponse.builder().status(OrderStatus.IN_PROGRESS).build()
            );

            when(orderRepository.findByStatusInOrderByCreatedAtAsc(any())).thenReturn(orders);
            when(orderMapper.toOrderResponseList(orders)).thenReturn(expectedResponses);

            List<OrderResponse> result = orderService.getOrderQueue();

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("getCurrentOrder")
    class GetCurrentOrder {

        @Test
        @DisplayName("Should return current in-progress order")
        void getCurrentOrder_exists() {
            Order order = createOrderWithStatus(UUID.randomUUID(), OrderStatus.IN_PROGRESS);
            OrderResponse expected = OrderResponse.builder().status(OrderStatus.IN_PROGRESS).build();

            when(orderRepository.findFirstByStatus(OrderStatus.IN_PROGRESS)).thenReturn(Optional.of(order));
            when(orderMapper.toOrderResponse(order)).thenReturn(expected);

            OrderResponse result = orderService.getCurrentOrder();

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should return null when no order is in progress")
        void getCurrentOrder_none() {
            when(orderRepository.findFirstByStatus(OrderStatus.IN_PROGRESS)).thenReturn(Optional.empty());

            OrderResponse result = orderService.getCurrentOrder();

            assertThat(result).isNull();
        }
    }

    private Order createOrderWithStatus(UUID id, OrderStatus status) {
        Order order = Order.builder()
                .trackingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerName("Test Customer")
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        order.setId(id);
        return order;
    }
}
