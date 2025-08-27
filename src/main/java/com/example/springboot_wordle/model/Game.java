package com.example.springboot_wordle.model;

import com.example.springboot_wordle.dto.GuessOutcome;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "games")
public class Game {
    private String id;
    private String ownerEmail;
    private String solution;
    private final int maxTries = 6;
    private int tries = 0;
    private boolean finished = false;
    private boolean won = false;
    private List<GuessResult> history = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant finishedAt;
    private GameLevel level;


    public GuessOutcome addGuessResult(String rawGuess, List<Color> feedback) {
        if (history == null) {
            history = new ArrayList<>();
        }
        String guess = rawGuess.toUpperCase();
        boolean correct = feedback.stream().allMatch(c -> c == Color.GREEN);
        history.add(new GuessResult(guess, feedback));
        tries ++;

        // Set the edge condition
        if (correct || tries >= maxTries) {
            finished = true;
            won = correct;
            finishedAt = Instant.now();
        }

        return new GuessOutcome(id, guess, feedback, correct, tries, maxTries, finished, history);
    }
}

