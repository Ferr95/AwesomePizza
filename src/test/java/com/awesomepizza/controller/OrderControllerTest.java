package com.awesomepizza.controller;

import com.awesomepizza.domain.enums.OrderStatus;
import com.awesomepizza.dto.response.OrderTrackingResponse;
import com.awesomepizza.exception.GlobalExceptionHandler;
import com.awesomepizza.exception.OrderNotFoundException;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders - Should create order and return 201")
    void createOrder_success() throws Exception {
        OrderTrackingResponse response = OrderTrackingResponse.builder()
                .trackingCode("ABC12345")
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderService.createOrder(any())).thenReturn(response);

        String requestBody = """
                {
                    "customerName": "John",
                    "items": [
                        {"pizzaId": "%s", "quantity": 2}
                    ]
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackingCode").value("ABC12345"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/orders - Should return 400 for empty items")
    void createOrder_emptyItems() throws Exception {
        String requestBody = """
                {
                    "customerName": "John",
                    "items": []
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/orders/{trackingCode} - Should return order status")
    void getOrderStatus_success() throws Exception {
        OrderTrackingResponse response = OrderTrackingResponse.builder()
                .trackingCode("ABC12345")
                .status(OrderStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderService.getOrderByTrackingCode("ABC12345")).thenReturn(response);

        mockMvc.perform(get("/api/orders/ABC12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingCode").value("ABC12345"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("GET /api/orders/{trackingCode} - Should return 404 for unknown code")
    void getOrderStatus_notFound() throws Exception {
        when(orderService.getOrderByTrackingCode("INVALID"))
                .thenThrow(new OrderNotFoundException("INVALID"));

        mockMvc.perform(get("/api/orders/INVALID"))
                .andExpect(status().isNotFound());
    }
}
