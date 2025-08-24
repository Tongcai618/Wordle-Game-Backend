package com.example.springboot_wordle.controller;

import com.example.springboot_wordle.dto.GuessOutcome;
import com.example.springboot_wordle.model.Color;
import com.example.springboot_wordle.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wordle/games")
public class GameController {

    @Autowired
    private GameService gameService;

    public GameController(GameService gameService) {

        this.gameService = gameService;
    }

    @GetMapping("/new")
    public Map<String, String> newGame(Authentication authentication) {
        String email = authentication.getName(); // comes from JWT subject
        String gameId = gameService.CreateGame(email);
        return Map.of("gameId", gameId);
    }

    @GetMapping("/guess")
    public Map<String, GuessOutcome> guess(
            @RequestParam("id") String id,
            @RequestParam("guess") String guess,
            Authentication authentication) {
        String email = authentication.getName();

        return Map.of("Result", gameService.submitGuess(email, id, guess));
    }
}
