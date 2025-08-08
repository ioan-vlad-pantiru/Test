package com.example.transport.server.controller;

import com.example.transport.server.dto.RideDto;
import com.example.transport.server.entity.Ride;
import com.example.transport.server.service.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RideControllerTest {

    @Mock
    private RideService rideService;

    @InjectMocks
    private RideController rideController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRides() {
        // Arrange
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setDestination("Paris");
        ride.setDepartureTime("2023-10-01T10:00:00");
        when(rideService.getAllRides()).thenReturn(Collections.singletonList(ride));

        // Act
        ResponseEntity<List<RideDto>> response = rideController.getAllRides();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Paris", response.getBody().get(0).getDestination());
    }

    @Test
    void testGetRideById() {
        // Arrange
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setDestination("Paris");
        ride.setDepartureTime("2023-10-01T10:00:00");
        when(rideService.getRideById(1L)).thenReturn(ride);

        // Act
        ResponseEntity<RideDto> response = rideController.getRideById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Paris", response.getBody().getDestination());
    }

    @Test
    void testGetRideById_NotFound() {
        // Arrange
        when(rideService.getRideById(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<RideDto> response = rideController.getRideById(1L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testCreateRide() {
        // Arrange
        RideDto rideDto = new RideDto(null, "Paris", "2023-10-01T10:00:00");
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setDestination("Paris");
        ride.setDepartureTime("2023-10-01T10:00:00");
        when(rideService.createRide(any(Ride.class))).thenReturn(ride);

        // Act
        ResponseEntity<RideDto> response = rideController.createRide(rideDto);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Paris", response.getBody().getDestination());
        assertEquals(URI.create("/api/rides/1"), response.getHeaders().getLocation());
    }

    @Test
    void testUpdateRide() {
        // Arrange
        RideDto rideDto = new RideDto(1L, "Paris", "2023-10-01T10:00:00");
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setDestination("Paris");
        ride.setDepartureTime("2023-10-01T10:00:00");
        when(rideService.updateRide(anyLong(), any(Ride.class))).thenReturn(ride);

        // Act
        ResponseEntity<RideDto> response = rideController.updateRide(1L, rideDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Paris", response.getBody().getDestination());
    }

    @Test
    void testDeleteRide() {
        // Arrange
        doNothing().when(rideService).deleteRide(1L);

        // Act
        ResponseEntity<Void> response = rideController.deleteRide(1L);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(rideService, times(1)).deleteRide(1L);
    }
}