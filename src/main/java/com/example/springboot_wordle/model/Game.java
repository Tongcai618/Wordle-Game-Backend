package com.example.springboot_wordle.model;

import com.example.springboot_wordle.dto.GuessOutcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Game {
    private String id;
    private String solution;
    private final int maxTries = 6;
    private int tries = 0;
    private boolean finished = false;
    private boolean won = false;
    private final List<GuessResult> history = new ArrayList<>();


    public synchronized GuessOutcome addGuessResult(String rawGuess, List<Color> feedback) {
        String guess = rawGuess.toUpperCase();
        boolean correct = feedback.stream().allMatch(c -> c == Color.GREEN);
        tries ++;

        // Set the edge condition
        if (correct || tries >= maxTries) {
            finished = true;
            won = correct;
        }

        return new GuessOutcome(id, guess, feedback, correct, tries, maxTries, finished);
    }
}

