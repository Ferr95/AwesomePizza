package com.awesomepizza.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderStatus State Machine")
class OrderStatusTest {

    @Nested
    @DisplayName("Valid transitions")
    class ValidTransitions {

        @Test
        @DisplayName("PENDING can transition to IN_PROGRESS")
        void pendingToInProgress() {
            assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("IN_PROGRESS can transition to READY")
        void inProgressToReady() {
            assertTrue(OrderStatus.IN_PROGRESS.canTransitionTo(OrderStatus.READY));
        }

        @Test
        @DisplayName("READY can transition to PICKED_UP")
        void readyToPickedUp() {
            assertTrue(OrderStatus.READY.canTransitionTo(OrderStatus.PICKED_UP));
        }
    }

    @Nested
    @DisplayName("Invalid transitions")
    class InvalidTransitions {

        @Test
        @DisplayName("PENDING cannot transition to READY (skipping IN_PROGRESS)")
        void pendingCannotSkipToReady() {
            assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.READY));
        }

        @Test
        @DisplayName("PENDING cannot transition to PICKED_UP")
        void pendingCannotGoToPickedUp() {
            assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.PICKED_UP));
        }

        @Test
        @DisplayName("IN_PROGRESS cannot go back to PENDING")
        void inProgressCannotGoBack() {
            assertFalse(OrderStatus.IN_PROGRESS.canTransitionTo(OrderStatus.PENDING));
        }

        @Test
        @DisplayName("READY cannot go back to IN_PROGRESS")
        void readyCannotGoBack() {
            assertFalse(OrderStatus.READY.canTransitionTo(OrderStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("PICKED_UP is a terminal state")
        void pickedUpIsTerminal() {
            for (OrderStatus status : OrderStatus.values()) {
                assertFalse(OrderStatus.PICKED_UP.canTransitionTo(status));
            }
        }

        @Test
        @DisplayName("Cannot transition to same state")
        void cannotTransitionToSelf() {
            for (OrderStatus status : OrderStatus.values()) {
                assertFalse(status.canTransitionTo(status));
            }
        }
    }
}
