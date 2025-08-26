// src/main/java/com/example/springboot_wordle/user/User.java
package com.example.springboot_wordle.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String username;

    @Email @NotBlank
    @Indexed(unique = true)
    private String email;

    @NotBlank
    private String passwordHash;

    @CreatedDate
    private Instant createdAt;

    private UserStats userStats;
}
