package com.example.transport.server.service;

import com.example.transport.server.entity.User;
import com.example.transport.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setPassword(password);

        when(userRepo.findByUsernameAndPassword(username, password)).thenReturn(expectedUser);

        // Act
        User actualUser = authService.login(username, password);

        // Assert
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testLogin_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        String password = "wrongPassword";

        when(userRepo.findByUsernameAndPassword(anyString(), anyString())).thenReturn(null);

        // Act
        User actualUser = authService.login(username, password);

        // Assert
        assertNull(actualUser);
    }

    @Test
    public void testLogin_EmptyUsername() {
        // Arrange
        String username = "";
        String password = "testPassword";

        // Act
        User actualUser = authService.login(username, password);

        // Assert
        assertNull(actualUser);
    }

    @Test
    public void testLogin_EmptyPassword() {
        // Arrange
        String username = "testUser";
        String password = "";

        // Act
        User actualUser = authService.login(username, password);

        // Assert
        assertNull(actualUser);
    }

    @Test
    public void testLogin_NullUsername() {
        // Arrange
        String username = null;
        String password = "testPassword";

        // Act
        User actualUser = authService.login(username, password);

        // Assert
        assertNull(actualUser);
    }

    @Test
    public void testLogin_NullPassword() {
        // Arrange
        String username = "testUser";
        String password = null;

        // Act
        User actualUser = authService.login(username, password);

        // Assert
        assertNull(actualUser);
    }
}