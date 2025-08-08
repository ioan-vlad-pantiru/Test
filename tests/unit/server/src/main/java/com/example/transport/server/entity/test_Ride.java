package com.example.transport.server.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideTest {

    private Ride ride;

    @BeforeEach
    void setUp() {
        ride = new Ride();
    }

    @Test
    void testGetId() {
        ride.setId(1L);
        assertEquals(1L, ride.getId());
    }

    @Test
    void testSetId() {
        ride.setId(2L);
        assertEquals(2L, ride.getId());
    }

    @Test
    void testGetStartLocation() {
        ride.setStartLocation("Location A");
        assertEquals("Location A", ride.getStartLocation());
    }

    @Test
    void testSetStartLocation() {
        ride.setStartLocation("Location B");
        assertEquals("Location B", ride.getStartLocation());
    }

    @Test
    void testGetEndLocation() {
        ride.setEndLocation("Location C");
        assertEquals("Location C", ride.getEndLocation());
    }

    @Test
    void testSetEndLocation() {
        ride.setEndLocation("Location D");
        assertEquals("Location D", ride.getEndLocation());
    }

    @Test
    void testGetDistance() {
        ride.setDistance(10.5);
        assertEquals(10.5, ride.getDistance());
    }

    @Test
    void testSetDistance() {
        ride.setDistance(20.0);
        assertEquals(20.0, ride.getDistance());
    }

    @Test
    void testGetDuration() {
        ride.setDuration(30);
        assertEquals(30, ride.getDuration());
    }

    @Test
    void testSetDuration() {
        ride.setDuration(45);
        assertEquals(45, ride.getDuration());
    }

    @Test
    void testEquals_SameObject() {
        assertEquals(ride, ride);
    }

    @Test
    void testEquals_NullObject() {
        assertNotEquals(ride, null);
    }

    @Test
    void testEquals_DifferentClass() {
        assertNotEquals(ride, new Object());
    }

    @Test
    void testHashCode() {
        ride.setId(1L);
        assertEquals(ride.hashCode(), ride.hashCode());
    }

    @Test
    void testToString() {
        ride.setId(1L);
        ride.setStartLocation("Location A");
        ride.setEndLocation("Location B");
        String expectedString = "Ride{id=1, startLocation='Location A', endLocation='Location B', distance=0.0, duration=0}";
        assertEquals(expectedString, ride.toString());
    }
}