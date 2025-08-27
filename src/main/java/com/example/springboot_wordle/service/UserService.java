package com.example.springboot_wordle.service;

import com.example.springboot_wordle.dto.GameDTO;
import com.example.springboot_wordle.model.Game;
import com.example.springboot_wordle.model.User;
import com.example.springboot_wordle.model.UserStats;
import com.example.springboot_wordle.repository.GameRepository;
import com.example.springboot_wordle.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public List<GameDTO> getGameActivity(Authentication authentication, int days) {
        String email = authentication.getName();

        Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);
        List<Game> games = gameRepository.findByOwnerEmailAndFinishedAtAfter(email, cutoff);

        // Convert game to gameDTOs and return
        return games.stream().map(GameDTO::new).collect(Collectors.toList());
    }
}
