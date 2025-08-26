package com.example.springboot_wordle.service;

import com.example.springboot_wordle.model.Game;
import com.example.springboot_wordle.model.User;
import com.example.springboot_wordle.model.UserStats;
import com.example.springboot_wordle.repository.GameRepository;
import com.example.springboot_wordle.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public UserService(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public User getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        // assuming `isWon()` getter
        long wins = gameRepository.findByOwnerEmail(email).size();

        User user = userRepository.findByEmail(email).orElse(null);
        UserStats myUserStats = new UserStats();
        myUserStats.setWins(wins);

        assert user != null;

        user.setUserStats(myUserStats);
        return user;
    }
}
