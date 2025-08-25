package com.example.springboot_wordle.model;
import java.util.List;

/**
 * @param feedback ["green","yellow","grey",...]
 */

public record GuessResult(String guess, List<Color> feedback) {
    public GuessResult(String guess, List<Color> feedback) {
        this.guess = guess.toUpperCase();
        this.feedback = feedback;
    }
}
