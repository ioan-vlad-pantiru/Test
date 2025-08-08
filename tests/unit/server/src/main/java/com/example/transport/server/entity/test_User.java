package com.example.transport.server.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("password123", "testuser");
    }

    @Test
    void testUserConstructor() {
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testGetId() {
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    void testSetId() {
        user.setId(2L);
        assertEquals(2L, user.getId());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testSetUsername() {
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetPassword() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void testUserDefaultConstructor() {
        User defaultUser = new User();
        assertNotNull(defaultUser);
        assertNull(defaultUser.getUsername());
        assertNull(defaultUser.getPassword());
    }
}