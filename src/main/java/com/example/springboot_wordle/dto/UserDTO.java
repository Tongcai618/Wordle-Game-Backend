package com.example.springboot_wordle.dto;

import com.example.springboot_wordle.model.User;
import com.example.springboot_wordle.model.UserStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private Instant createdAt;
    private UserStats userStats;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.userStats = user.getUserStats();
    }
}
