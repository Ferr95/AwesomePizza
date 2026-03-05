package com.awesomepizza.exception;

import com.awesomepizza.domain.enums.OrderStatus;

/** Thrown when an invalid status transition is attempted (e.g. PENDING -> READY). Returns 400. */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
