package com.example.springboot_wordle.controller;


import com.example.springboot_wordle.dto.AuthResponse;
import com.example.springboot_wordle.dto.LoginRequest;
import com.example.springboot_wordle.dto.SignupRequest;
import com.example.springboot_wordle.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest req) {
        return authService.signup(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
