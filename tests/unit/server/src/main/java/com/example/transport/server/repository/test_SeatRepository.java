package com.example.transport.server.repository;

import com.example.transport.server.entity.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class SeatRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private SeatRepository seatRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_ExistingSeat() {
        // Arrange
        Long seatId = 1L;
        Seat expectedSeat = new Seat();
        expectedSeat.setId(seatId);
        
        TypedQuery<Seat> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Seat.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyLong())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(expectedSeat);

        // Act
        Seat actualSeat = seatRepository.findById(seatId);

        // Assert
        assertNotNull(actualSeat);
        assertEquals(expectedSeat.getId(), actualSeat.getId());
        verify(entityManager).createQuery(anyString(), eq(Seat.class));
        verify(query).setParameter("id", seatId);
        verify(query).getSingleResult();
    }

    @Test
    public void testFindById_NonExistingSeat() {
        // Arrange
        Long seatId = 2L;

        TypedQuery<Seat> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Seat.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyLong())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new javax.persistence.NoResultException());

        // Act
        Seat actualSeat = seatRepository.findById(seatId);

        // Assert
        assertNull(actualSeat);
        verify(entityManager).createQuery(anyString(), eq(Seat.class));
        verify(query).setParameter("id", seatId);
        verify(query).getSingleResult();
    }

    // Additional tests can be added here for other methods in SeatRepository
}