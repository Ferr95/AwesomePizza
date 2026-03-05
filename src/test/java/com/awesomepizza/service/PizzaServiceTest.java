package com.awesomepizza.service;

import com.awesomepizza.domain.entity.Pizza;
import com.awesomepizza.dto.response.PizzaResponse;
import com.awesomepizza.exception.PizzaNotFoundException;
import com.awesomepizza.mapper.OrderMapper;
import com.awesomepizza.repository.PizzaRepository;
import com.awesomepizza.service.impl.PizzaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PizzaService")
class PizzaServiceTest {

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private PizzaServiceImpl pizzaService;

    @Test
    @DisplayName("Should return all pizzas")
    void getAllPizzas() {
        Pizza margherita = Pizza.builder().name("Margherita").price(new BigDecimal("8.50")).build();
        Pizza pepperoni = Pizza.builder().name("Pepperoni").price(new BigDecimal("10.00")).build();

        PizzaResponse margheritaResponse = PizzaResponse.builder().name("Margherita").price(new BigDecimal("8.50")).build();
        PizzaResponse pepperoniResponse = PizzaResponse.builder().name("Pepperoni").price(new BigDecimal("10.00")).build();

        when(pizzaRepository.findAll()).thenReturn(List.of(margherita, pepperoni));
        when(orderMapper.toPizzaResponse(margherita)).thenReturn(margheritaResponse);
        when(orderMapper.toPizzaResponse(pepperoni)).thenReturn(pepperoniResponse);

        List<PizzaResponse> result = pizzaService.getAllPizzas();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Margherita");
        assertThat(result.get(1).getName()).isEqualTo("Pepperoni");
    }

    @Test
    @DisplayName("Should return pizza by ID")
    void getPizzaById_success() {
        UUID id = UUID.randomUUID();
        Pizza pizza = Pizza.builder().name("Margherita").price(new BigDecimal("8.50")).build();
        pizza.setId(id);

        PizzaResponse expected = PizzaResponse.builder().id(id).name("Margherita").price(new BigDecimal("8.50")).build();

        when(pizzaRepository.findById(id)).thenReturn(Optional.of(pizza));
        when(orderMapper.toPizzaResponse(pizza)).thenReturn(expected);

        PizzaResponse result = pizzaService.getPizzaById(id);

        assertThat(result.getName()).isEqualTo("Margherita");
    }

    @Test
    @DisplayName("Should throw PizzaNotFoundException for non-existent ID")
    void getPizzaById_notFound() {
        UUID id = UUID.randomUUID();
        when(pizzaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pizzaService.getPizzaById(id))
                .isInstanceOf(PizzaNotFoundException.class);
    }
}
