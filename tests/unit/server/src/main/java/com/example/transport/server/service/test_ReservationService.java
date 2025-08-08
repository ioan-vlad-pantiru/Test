package com.example.transport.server.service;

import com.example.transport.server.entity.Ride;
import com.example.transport.server.entity.User;
import com.example.transport.server.entity.Seat;
import com.example.transport.server.entity.Reservation;
import com.example.transport.server.repository.RideRepository;
import com.example.transport.server.repository.UserRepository;
import com.example.transport.server.repository.SeatRepository;
import com.example.transport.server.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @Mock
    private RideRepository rideRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private SeatRepository seatRepo;

    @Mock
    private ReservationRepository reservationRepo;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateReservation_Success() {
        // Arrange
        User user = new User();
        Ride ride = new Ride();
        Seat seat = new Seat();
        Reservation reservation = new Reservation(user, ride, seat);

        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(rideRepo.findById(any(Long.class))).thenReturn(Optional.of(ride));
        when(seatRepo.findById(any(Long.class))).thenReturn(Optional.of(seat));
        when(reservationRepo.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Reservation result = reservationService.createReservation(1L, 1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(ride, result.getRide());
        assertEquals(seat, result.getSeat());
        verify(reservationRepo, times(1)).save(any(Reservation.class));
    }

    @Test
    public void testCreateReservation_UserNotFound() {
        // Arrange
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationService.createReservation(1L, 1L, 1L));
        verify(reservationRepo, never()).save(any(Reservation.class));
    }

    @Test
    public void testCreateReservation_RideNotFound() {
        // Arrange
        User user = new User();
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(rideRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationService.createReservation(1L, 1L, 1L));
        verify(reservationRepo, never()).save(any(Reservation.class));
    }

    @Test
    public void testCreateReservation_SeatNotFound() {
        // Arrange
        User user = new User();
        Ride ride = new Ride();
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(rideRepo.findById(any(Long.class))).thenReturn(Optional.of(ride));
        when(seatRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationService.createReservation(1L, 1L, 1L));
        verify(reservationRepo, never()).save(any(Reservation.class));
    }

    @Test
    public void testCancelReservation_Success() {
        // Arrange
        Reservation reservation = new Reservation();
        when(reservationRepo.findById(any(Long.class))).thenReturn(Optional.of(reservation));

        // Act
        reservationService.cancelReservation(1L);

        // Assert
        verify(reservationRepo, times(1)).delete(reservation);
    }

    @Test
    public void testCancelReservation_NotFound() {
        // Arrange
        when(reservationRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationService.cancelReservation(1L));
        verify(reservationRepo, never()).delete(any(Reservation.class));
    }
}