package com.example.transport.server.service;

import com.example.transport.server.entity.User;
import com.example.transport.server.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepo;
    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String username, String password) {
        // Validate user credentials
        return userRepo.findByUsernameAndPassword(username, password);
    }
}