package com.example.springboot_wordle.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String username
) {}
