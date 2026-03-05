package com.awesomepizza.controller;

import com.awesomepizza.domain.enums.OrderStatus;
import com.awesomepizza.dto.response.OrderResponse;
import com.awesomepizza.exception.InvalidStatusTransitionException;
import com.awesomepizza.exception.OrderAlreadyInProgressException;
import com.awesomepizza.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PizzaioloController.class)
@DisplayName("PizzaioloController")
class PizzaioloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("GET /api/pizzaiolo/orders - Should return order queue")
    void getOrderQueue() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderResponse order = OrderResponse.builder()
                .id(orderId)
                .trackingCode("ABC12345")
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(orderService.getOrderQueue()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/pizzaiolo/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trackingCode").value("ABC12345"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/pizzaiolo/orders/current - Should return current order")
    void getCurrentOrder_exists() throws Exception {
        OrderResponse order = OrderResponse.builder()
                .trackingCode("ABC12345")
                .status(OrderStatus.IN_PROGRESS)
                .items(Collections.emptyList())
                .build();

        when(orderService.getCurrentOrder()).thenReturn(order);

        mockMvc.perform(get("/api/pizzaiolo/orders/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("GET /api/pizzaiolo/orders/current - Should return 204 when no current order")
    void getCurrentOrder_none() throws Exception {
        when(orderService.getCurrentOrder()).thenReturn(null);

        mockMvc.perform(get("/api/pizzaiolo/orders/current"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /api/pizzaiolo/orders/{id}/status - Should update status")
    void updateOrderStatus_success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderResponse response = OrderResponse.builder()
                .id(orderId)
                .status(OrderStatus.IN_PROGRESS)
                .items(Collections.emptyList())
                .build();

        when(orderService.updateOrderStatus(eq(orderId), any())).thenReturn(response);

        String requestBody = """
                {"status": "IN_PROGRESS"}
                """;

        mockMvc.perform(put("/api/pizzaiolo/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("PUT /api/pizzaiolo/orders/{id}/status - Should return 400 for invalid transition")
    void updateOrderStatus_invalidTransition() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.updateOrderStatus(eq(orderId), any()))
                .thenThrow(new InvalidStatusTransitionException(OrderStatus.PENDING, OrderStatus.READY));

        String requestBody = """
                {"status": "READY"}
                """;

        mockMvc.perform(put("/api/pizzaiolo/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/pizzaiolo/orders/{id}/status - Should return 409 when order already in progress")
    void updateOrderStatus_conflict() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.updateOrderStatus(eq(orderId), any()))
                .thenThrow(new OrderAlreadyInProgressException());

        String requestBody = """
                {"status": "IN_PROGRESS"}
                """;

        mockMvc.perform(put("/api/pizzaiolo/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }
}
