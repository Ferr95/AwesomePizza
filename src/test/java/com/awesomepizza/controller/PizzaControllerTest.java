package com.awesomepizza.controller;

import com.awesomepizza.dto.response.PizzaResponse;
import com.awesomepizza.exception.PizzaNotFoundException;
import com.awesomepizza.service.PizzaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PizzaController.class)
@DisplayName("PizzaController")
class PizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PizzaService pizzaService;

    @Test
    @DisplayName("GET /api/pizzas - Should return all pizzas")
    void getAllPizzas() throws Exception {
        List<PizzaResponse> pizzas = List.of(
                PizzaResponse.builder().id(UUID.randomUUID()).name("Margherita").price(new BigDecimal("8.50")).build(),
                PizzaResponse.builder().id(UUID.randomUUID()).name("Pepperoni").price(new BigDecimal("10.00")).build()
        );

        when(pizzaService.getAllPizzas()).thenReturn(pizzas);

        mockMvc.perform(get("/api/pizzas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Margherita"))
                .andExpect(jsonPath("$[1].name").value("Pepperoni"));
    }

    @Test
    @DisplayName("GET /api/pizzas/{id} - Should return pizza by ID")
    void getPizzaById_success() throws Exception {
        UUID id = UUID.randomUUID();
        PizzaResponse pizza = PizzaResponse.builder()
                .id(id).name("Margherita").description("Classic").price(new BigDecimal("8.50")).build();

        when(pizzaService.getPizzaById(id)).thenReturn(pizza);

        mockMvc.perform(get("/api/pizzas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Margherita"));
    }

    @Test
    @DisplayName("GET /api/pizzas/{id} - Should return 404 for non-existent pizza")
    void getPizzaById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(pizzaService.getPizzaById(id)).thenThrow(new PizzaNotFoundException(id));

        mockMvc.perform(get("/api/pizzas/{id}", id))
                .andExpect(status().isNotFound());
    }
}
