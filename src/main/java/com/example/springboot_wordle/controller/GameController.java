package com.example.springboot_wordle.controller;

import com.example.springboot_wordle.dto.GuessOutcome;
import com.example.springboot_wordle.model.Color;
import com.example.springboot_wordle.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wordle")
public class GameController {

    @Autowired
    private GameService gameService;

    public GameController(GameService gameService) {

        this.gameService = gameService;
    }

    @GetMapping("/new")
    public Map<String, String> newGame() {
        return Map.of("gameId", gameService.CreateGame());
    }

    @GetMapping("/guess")
    public Map<String, GuessOutcome> guess(
            @RequestParam("id") String id,
            @RequestParam("guess") String guess) {

        return Map.of("Result", gameService.submitGuess(id, guess));
    }
}
