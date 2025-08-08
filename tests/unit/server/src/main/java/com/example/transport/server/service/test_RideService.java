package com.example.transport.server.service;

import com.example.transport.server.entity.Ride;
import com.example.transport.server.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RideServiceTest {

    @Mock
    private RideRepository rideRepo;

    @InjectMocks
    private RideService rideService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRide_ShouldSaveRide_WhenRideIsValid() {
        // Arrange
        Ride ride = new Ride();
        ride.setDate(LocalDate.now());
        ride.setDestination("City Center");
        ride.setOrigin("Airport");

        when(rideRepo.save(any(Ride.class))).thenReturn(ride);

        // Act
        Ride createdRide = rideService.createRide(ride);

        // Assert
        assertNotNull(createdRide);
        assertNull(createdRide.getId()); // ID should be null before saving
        assertEquals("City Center", createdRide.getDestination());
        assertEquals("Airport", createdRide.getOrigin());

        // Verify that save was called
        verify(rideRepo, times(1)).save(any(Ride.class));
    }

    @Test
    void createRide_ShouldThrowException_WhenRideIsNull() {
        // Arrange
        Ride ride = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> rideService.createRide(ride));
    }

    @Test
    void createRide_ShouldHandleDatabaseError_WhenSaveFails() {
        // Arrange
        Ride ride = new Ride();
        ride.setDate(LocalDate.now());
        ride.setDestination("City Center");
        ride.setOrigin("Airport");

        when(rideRepo.save(any(Ride.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> rideService.createRide(ride));
        assertEquals("Database error", thrown.getMessage());
    }

    // Additional tests for other methods in RideService can be added here
}