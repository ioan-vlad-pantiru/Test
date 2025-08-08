package com.example.transport.server.repository;

import com.example.transport.server.entity.Reservation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ReservationRepository reservationRepository;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testReservation = new Reservation();
        testReservation.setId(1L);
        // Set other properties of testReservation as needed
    }

    @Test
    void testFindById_WhenReservationExists() {
        when(entityManager.find(Reservation.class, 1L)).thenReturn(testReservation);

        Reservation foundReservation = reservationRepository.findById(1L);

        assertNotNull(foundReservation);
        assertEquals(testReservation.getId(), foundReservation.getId());
        verify(entityManager, times(1)).find(Reservation.class, 1L);
    }

    @Test
    void testFindById_WhenReservationDoesNotExist() {
        when(entityManager.find(Reservation.class, 2L)).thenReturn(null);

        Reservation foundReservation = reservationRepository.findById(2L);

        assertNull(foundReservation);
        verify(entityManager, times(1)).find(Reservation.class, 2L);
    }

    @Test
    void testFindAll() {
        when(entityManager.createQuery(any(CriteriaQuery.class))).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(anyString(), eq(Reservation.class))).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(anyString(), eq(Reservation.class)).getResultList()).thenReturn(Collections.singletonList(testReservation));

        List<Reservation> reservations = reservationRepository.findAll();

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals(testReservation.getId(), reservations.get(0).getId());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Reservation.class));
    }

    @Test
    void testSave() {
        reservationRepository.save(testReservation);

        verify(entityManager, times(1)).persist(testReservation);
    }

    @Test
    void testDelete() {
        reservationRepository.delete(testReservation);

        verify(entityManager, times(1)).remove(testReservation);
    }
}