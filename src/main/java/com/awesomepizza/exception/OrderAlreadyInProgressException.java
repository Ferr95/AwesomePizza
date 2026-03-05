package com.awesomepizza.exception;

/** Thrown when attempting to take on an order while another is already IN_PROGRESS. Returns 409. */
public class OrderAlreadyInProgressException extends RuntimeException {

    public OrderAlreadyInProgressException() {
        super("There is already an order in progress. Complete it before taking a new one.");
    }
}
