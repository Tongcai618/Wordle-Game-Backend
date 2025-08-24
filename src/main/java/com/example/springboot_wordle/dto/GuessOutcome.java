package com.example.springboot_wordle.dto;

import com.example.springboot_wordle.model.Color;

import java.util.List;

// The outcome of a guess
public record GuessOutcome(
        String gameId,
        String guess,
        List<Color> feedback,
        boolean correct,
        int tries,
        int maxTries,
        boolean finished
) {
}