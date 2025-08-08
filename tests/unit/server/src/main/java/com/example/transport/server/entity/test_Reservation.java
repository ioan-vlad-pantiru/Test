package com.example.transport.server.entity;

import com.example.transport.server.entity.Reservation;
import com.example.transport.server.entity.Ride;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private Reservation reservation;
    private Ride ride;

    @BeforeEach
    void setUp() {
        ride = new Ride(); // Assuming Ride has a default constructor
        reservation = new Reservation();
        reservation.setRide(ride);
        reservation.setCustomerName("John Doe");
        reservation.setSeatsReserved(2);
    }

    @Test
    void testGetId() {
        assertNull(reservation.getId(), "New reservation should not have an ID");
    }

    @Test
    void testGetRide() {
        assertEquals(ride, reservation.getRide(), "The ride should match the assigned ride");
    }

    @Test
    void testGetCustomerName() {
        assertEquals("John Doe", reservation.getCustomerName(), "Customer name should match");
    }

    @Test
    void testGetSeatsReserved() {
        assertEquals(2, reservation.getSeatsReserved(), "Seats reserved should match the assigned value");
    }

    @Test
    void testSetRide() {
        Ride newRide = new Ride(); // Assuming another ride instance
        reservation.setRide(newRide);
        assertEquals(newRide, reservation.getRide(), "The ride should be updated correctly");
    }

    @Test
    void testSetCustomerName() {
        reservation.setCustomerName("Jane Doe");
        assertEquals("Jane Doe", reservation.getCustomerName(), "Customer name should be updated correctly");
    }

    @Test
    void testSetSeatsReserved() {
        reservation.setSeatsReserved(5);
        assertEquals(5, reservation.getSeatsReserved(), "Seats reserved should be updated correctly");
    }

    @Test
    void testConstructorWithParameters() {
        Reservation newReservation = new Reservation(ride, "Alice", 3);
        assertEquals(ride, newReservation.getRide(), "Ride should match the provided ride");
        assertEquals("Alice", newReservation.getCustomerName(), "Customer name should match the provided name");
        assertEquals(3, newReservation.getSeatsReserved(), "Seats reserved should match the provided value");
    }

    @Test
    void testInvalidCustomerName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservation.setCustomerName(null);
        });
        assertEquals("Customer name cannot be null", exception.getMessage());
    }

    @Test
    void testInvalidSeatsReserved() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservation.setSeatsReserved(-1);
        });
        assertEquals("Seats reserved cannot be negative", exception.getMessage());
    }
}