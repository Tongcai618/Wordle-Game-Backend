package com.example.springboot_wordle.model;

import lombok.Getter;

import java.util.List;

@Getter
public class GuessResult {
    private final String guess;
    private final List<String> feedback; // ["green","yellow","grey",...]

    public GuessResult(String guess, List<String> feedback) {
        this.guess = guess.toUpperCase();
        this.feedback = feedback;
    }

    public boolean isCorrect() {
        return feedback.stream().allMatch("green"::equals);
    }
}
