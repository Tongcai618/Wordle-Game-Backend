package com.example.springboot_wordle.service;

import com.example.springboot_wordle.dto.GameDTO;
import com.example.springboot_wordle.dto.UserDTO;
import com.example.springboot_wordle.model.Game;
import com.example.springboot_wordle.model.GameLevel;
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
import java.util.logging.Level;
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

        // Count wins by level
        long simpleWins = gameRepository.findByOwnerEmail(email).stream()
                .filter(game -> GameLevel.SIMPLE.equals(game.getLevel()))
                .count();

        long normalWins = gameRepository.findByOwnerEmail(email).stream()
                .filter(game -> GameLevel.NORMAL.equals(game.getLevel()))
                .count();

        // Get the user or throw if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        // Attach stats
        UserStats myUserStats = new UserStats();
        myUserStats.setSimpleWins(simpleWins);
        myUserStats.setNormalWins(normalWins);

        user.setUserStats(myUserStats);
        return user;
    }


    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        String email = user.getEmail();
        long simpleWins = gameRepository.findByOwnerEmail(email).stream()
                .filter(game -> GameLevel.SIMPLE.equals(game.getLevel()))
                .count();
        long normalWins = gameRepository.findByOwnerEmail(email).stream()
                .filter(game -> GameLevel.NORMAL.equals(game.getLevel()))
                .count();

        UserStats otherUserStats = new UserStats();
        otherUserStats.setSimpleWins(simpleWins);
        otherUserStats.setNormalWins(normalWins);

        user.setUserStats(otherUserStats);
        return new UserDTO(user);
    }


    public List<GameDTO> getGameActivity(Authentication authentication, int days) {
        String email = authentication.getName();

        Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);
        List<Game> games = gameRepository.findByOwnerEmailAndFinishedAtAfter(email, cutoff);

        // Convert game to gameDTOs and return
        return games.stream().map(GameDTO::new).collect(Collectors.toList());
    }


    public List<GameDTO> getGameActivityUsername(String username, int days) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        String email = user.getEmail();

        Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);
        List<Game> games = gameRepository.findByOwnerEmailAndFinishedAtAfter(email, cutoff);

        // Convert game to gameDTOs and return
        return games.stream().map(GameDTO::new).collect(Collectors.toList());
    }
}
