package com.example.transport.server.repository;

import com.example.transport.server.entity.Ride;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RideRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RideRepository rideRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllReturnsListOfRides() {
        // Arrange
        List<Ride> expectedRides = new ArrayList<>();
        expectedRides.add(new Ride()); // Add a sample Ride object
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Ride> criteriaQuery = mock(CriteriaQuery.class);
        Root<Ride> root = mock(Root.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Ride.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Ride.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(mock(javax.persistence.TypedQuery.class));
        when(entityManager.createQuery(criteriaQuery).getResultList()).thenReturn(expectedRides);

        // Act
        List<Ride> actualRides = rideRepository.findAll();

        // Assert
        assertEquals(expectedRides, actualRides);
        verify(entityManager, times(1)).getCriteriaBuilder();
        verify(entityManager, times(1)).createQuery(criteriaQuery);
    }

    @Test
    void testFindAllHandlesEmptyResult() {
        // Arrange
        List<Ride> expectedRides = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Ride> criteriaQuery = mock(CriteriaQuery.class);
        Root<Ride> root = mock(Root.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Ride.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Ride.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(mock(javax.persistence.TypedQuery.class));
        when(entityManager.createQuery(criteriaQuery).getResultList()).thenReturn(expectedRides);

        // Act
        List<Ride> actualRides = rideRepository.findAll();

        // Assert
        assertEquals(expectedRides, actualRides);
        verify(entityManager, times(1)).getCriteriaBuilder();
        verify(entityManager, times(1)).createQuery(criteriaQuery);
    }

    // Additional tests can be added here for other methods in RideRepository
}