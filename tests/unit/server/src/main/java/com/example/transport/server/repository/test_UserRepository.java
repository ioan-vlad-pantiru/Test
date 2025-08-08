package com.example.transport.server.repository;

import com.example.transport.server.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
    }

    @Test
    public void testFindById_UserExists() {
        when(entityManager.find(User.class, 1L)).thenReturn(user);

        User foundUser = userRepository.findById(1L);

        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getName());
        verify(entityManager, times(1)).find(User.class, 1L);
    }

    @Test
    public void testFindById_UserDoesNotExist() {
        when(entityManager.find(User.class, 2L)).thenReturn(null);

        User foundUser = userRepository.findById(2L);

        assertNull(foundUser);
        verify(entityManager, times(1)).find(User.class, 2L);
    }

    @Test
    public void testFindAll_UsersExist() {
        List<User> users = Arrays.asList(user);
        TypedQuery<User> query = mock(TypedQuery.class);
        when(entityManager.createQuery(any(CriteriaQuery.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(users);

        List<User> foundUsers = userRepository.findAll();

        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals("Test User", foundUsers.get(0).getName());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }

    @Test
    public void testFindAll_NoUsers() {
        TypedQuery<User> query = mock(TypedQuery.class);
        when(entityManager.createQuery(any(CriteriaQuery.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<User> foundUsers = userRepository.findAll();

        assertNotNull(foundUsers);
        assertTrue(foundUsers.isEmpty());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }
}