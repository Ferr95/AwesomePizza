package com.awesomepizza.controller;

import com.awesomepizza.dto.response.PizzaResponse;
import com.awesomepizza.service.PizzaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for the pizza menu.
 * Public endpoints to browse available pizzas and their prices.
 */
@RestController
@RequestMapping("/api/pizzas")
@RequiredArgsConstructor
@Tag(name = "Pizza Menu", description = "Browse available pizzas")
public class PizzaController {

    private final PizzaService pizzaService;

    @GetMapping
    @Operation(summary = "Get all available pizzas")
    public ResponseEntity<List<PizzaResponse>> getAllPizzas() {
        return ResponseEntity.ok(pizzaService.getAllPizzas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pizza by ID")
    public ResponseEntity<PizzaResponse> getPizzaById(@PathVariable UUID id) {
        return ResponseEntity.ok(pizzaService.getPizzaById(id));
    }
}
