package com.awesomepizza.exception;

import java.util.UUID;

/** Thrown when a pizza is not found by ID (e.g. non-existent ID in an order). Returns 404. */
public class PizzaNotFoundException extends RuntimeException {

    public PizzaNotFoundException(UUID id) {
        super("Pizza not found with id: " + id);
    }
}
