package com.awesomepizza.domain.enums;

import java.util.Set;

/**
 * State machine for the lifecycle of an order.
 *
 * Valid transitions (forward only, never backward):
 *   PENDING --> IN_PROGRESS --> READY --> PICKED_UP
 *
 * - PENDING:     order received, waiting in the FIFO queue
 * - IN_PROGRESS: the pizzaiolo is preparing the order (only one at a time)
 * - READY:       pizza is ready, awaiting pickup
 * - PICKED_UP:   customer has picked up the order (terminal state)
 */
public enum OrderStatus {

    PENDING,
    IN_PROGRESS,
    READY,
    PICKED_UP;

    // Each status defines the directly reachable states
    private static final Set<OrderStatus> PENDING_TRANSITIONS = Set.of(IN_PROGRESS);
    private static final Set<OrderStatus> IN_PROGRESS_TRANSITIONS = Set.of(READY);
    private static final Set<OrderStatus> READY_TRANSITIONS = Set.of(PICKED_UP);
    private static final Set<OrderStatus> PICKED_UP_TRANSITIONS = Set.of(); // terminal state

    /** Checks whether the transition from the current state to the target state is allowed */
    public boolean canTransitionTo(OrderStatus target) {
        return getAllowedTransitions().contains(target);
    }

    /** Returns the set of valid transitions for the current state */
    private Set<OrderStatus> getAllowedTransitions() {
        return switch (this) {
            case PENDING -> PENDING_TRANSITIONS;
            case IN_PROGRESS -> IN_PROGRESS_TRANSITIONS;
            case READY -> READY_TRANSITIONS;
            case PICKED_UP -> PICKED_UP_TRANSITIONS;
        };
    }
}
