package com.example.springboot_wordle.controller;

import com.example.springboot_wordle.dto.GuessOutcome;
import com.example.springboot_wordle.model.Game;
import com.example.springboot_wordle.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wordle/games")
public class GameController {

    @Autowired
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    public Map<String, String> newGame(Authentication authentication) {
        String email = authentication.getName(); // comes from JWT subject
        String gameId = gameService.createGame(email);
        return Map.of("gameId", gameId);
    }

    @PostMapping("/refresh")
    public Map<String, String> refreshGame(Authentication authentication) {
        String email = authentication.getName();
        String gameId = gameService.refreshGame(email);
        return Map.of("gameId", gameId);
    }

    @GetMapping("/load")
    public Map<String, Game> loadGame(Authentication authentication,
                                      @RequestParam("id") String id) {
        String email = authentication.getName();
        return Map.of("game", gameService.loadGame(id, email));
    }

    @PostMapping("/guess")
    public Map<String, GuessOutcome> guess(
            @RequestParam("id") String id,
            @RequestParam("guess") String guess,
            Authentication authentication) {
        String email = authentication.getName();

        return Map.of("Result", gameService.submitGuess(email, id, guess));
    }
}
