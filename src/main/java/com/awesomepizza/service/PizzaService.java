package com.awesomepizza.service;

import com.awesomepizza.dto.response.PizzaResponse;

import java.util.List;
import java.util.UUID;

/**
 * Pizza service interface.
 * Provides read-only access to the available pizza menu.
 */
public interface PizzaService {

    /** Returns all pizzas on the menu */
    List<PizzaResponse> getAllPizzas();

    /** Returns a pizza by ID, or throws PizzaNotFoundException */
    PizzaResponse getPizzaById(UUID id);
}
