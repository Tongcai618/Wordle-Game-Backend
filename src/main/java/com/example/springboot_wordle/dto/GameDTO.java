package com.example.springboot_wordle.dto;

import com.example.springboot_wordle.model.Game;
import com.example.springboot_wordle.model.GameLevel;
import com.example.springboot_wordle.model.GuessResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private String id;
    private String ownerEmail;
    private int maxTries;
    private int tries;
    private boolean finished;
    private boolean won;
    private GameLevel level;
    private List<GuessResult> history;
    private Instant finishedAt;

    public GameDTO(Game game) {
        this.id = game.getId();
        this.ownerEmail = game.getOwnerEmail();
        this.maxTries = game.getMaxTries();
        this.tries = game.getTries();
        this.finished = game.isFinished();
        this.won = game.isWon();
        this.level = game.getLevel();
        this.history = game.getHistory();
        this.finishedAt = game.getFinishedAt();
    }

}
