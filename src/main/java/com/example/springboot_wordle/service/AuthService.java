package com.example.springboot_wordle.service;

import com.example.springboot_wordle.dto.AuthResponse;
import com.example.springboot_wordle.dto.LoginRequest;
import com.example.springboot_wordle.dto.SignupRequest;
import com.example.springboot_wordle.model.User;
import com.example.springboot_wordle.repository.UserRepository;
import com.example.springboot_wordle.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwt) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    public AuthResponse signup(SignupRequest req) {
        String email = req.email().trim().toLowerCase();
        String username = req.username();
        Instant now = Instant.now();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already registered");
        }

        String hash = passwordEncoder.encode(req.password());
        User user = User.builder()
                .email(email)
                .username(username)
                .passwordHash(hash)
                .createdAt(now)
                .build();
        userRepository.save(user);

        String token = jwt.generateToken(email);
        return new AuthResponse("Signup successful", token);
    }


    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwt.generateToken(email);
        return new AuthResponse("Login successful", token);
    }
}