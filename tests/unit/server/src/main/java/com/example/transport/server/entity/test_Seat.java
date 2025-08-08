package com.example.transport.server.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    private Seat seat;

    @BeforeEach
    void setUp() {
        seat = new Seat();
    }

    @Test
    void testSeatInitialization() {
        assertNotNull(seat);
        assertNull(seat.getId());
        assertNull(seat.getRide());
        assertFalse(seat.isReserved());
    }

    @Test
    void testSetId() {
        Long id = 1L;
        seat.setId(id);
        assertEquals(id, seat.getId());
    }

    @Test
    void testSetRide() {
        Ride ride = new Ride();
        seat.setRide(ride);
        assertEquals(ride, seat.getRide());
    }

    @Test
    void testReserveSeat() {
        seat.reserve();
        assertTrue(seat.isReserved());
    }

    @Test
    void testReleaseSeat() {
        seat.reserve(); // First reserve the seat
        seat.release();
        assertFalse(seat.isReserved());
    }

    @Test
    void testReserveAlreadyReservedSeat() {
        seat.reserve();
        Exception exception = assertThrows(IllegalStateException.class, seat::reserve);
        assertEquals("Seat is already reserved", exception.getMessage());
    }

    @Test
    void testReleaseUnreservedSeat() {
        Exception exception = assertThrows(IllegalStateException.class, seat::release);
        assertEquals("Seat is not reserved", exception.getMessage());
    }
}