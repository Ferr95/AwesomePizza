package com.awesomepizza.exception;

/** Thrown when an order is not found by tracking code or ID. Returns 404. */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String trackingCode) {
        super("Order not found with tracking code: " + trackingCode);
    }
}
