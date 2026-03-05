package com.awesomepizza.config;

import com.awesomepizza.domain.entity.Pizza;
import com.awesomepizza.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data initializer executed at application startup.
 * If the pizza table is empty, inserts 5 default pizzas into the menu.
 * Implements CommandLineRunner: the run() method is called automatically by Spring Boot after startup.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PizzaRepository pizzaRepository;

    /** Inserts default pizzas only if the menu is empty (avoids duplicates on restart) */
    @Override
    public void run(String... args) {
        if (pizzaRepository.count() == 0) {
            List<Pizza> pizzas = List.of(
                    Pizza.builder()
                            .name("Margherita")
                            .description("Tomato sauce, mozzarella, fresh basil")
                            .price(new BigDecimal("8.50"))
                            .build(),
                    Pizza.builder()
                            .name("Pepperoni")
                            .description("Tomato sauce, mozzarella, pepperoni")
                            .price(new BigDecimal("10.00"))
                            .build(),
                    Pizza.builder()
                            .name("Quattro Formaggi")
                            .description("Mozzarella, gorgonzola, parmesan, fontina")
                            .price(new BigDecimal("11.50"))
                            .build(),
                    Pizza.builder()
                            .name("Diavola")
                            .description("Tomato sauce, mozzarella, spicy salami, chili flakes")
                            .price(new BigDecimal("10.50"))
                            .build(),
                    Pizza.builder()
                            .name("Capricciosa")
                            .description("Tomato sauce, mozzarella, ham, mushrooms, artichokes, olives")
                            .price(new BigDecimal("12.00"))
                            .build()
            );
            pizzaRepository.saveAll(pizzas);
        }
    }
}
