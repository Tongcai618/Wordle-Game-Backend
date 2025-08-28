package com.example.springboot_wordle.service;


import com.example.springboot_wordle.dto.GameDTO;
import com.example.springboot_wordle.dto.GuessOutcome;
import com.example.springboot_wordle.model.Color;
import com.example.springboot_wordle.model.Game;
import com.example.springboot_wordle.model.GameLevel;
import com.example.springboot_wordle.repository.GameRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final List<String> normalWordList;
    private final List<String> simpleWordList;
    private final RedisTemplate<String, Game> gameRedisTemplate;
    private static final Duration TTL = Duration.ofMinutes(30);
    private final LeaderboardService leaderboardService;


    public GameService(ObjectMapper objectMapper, GameRepository gameRepository, RedisTemplate<String, Game> redisTemplate, LeaderboardService leaderboardService) {
        this.normalWordList = loadWordList(objectMapper, "WordList.json");
        this.simpleWordList = loadWordList(objectMapper, "SimpleWordList.json");
        this.gameRepository = gameRepository;
        this.gameRedisTemplate = redisTemplate;
        this.leaderboardService = leaderboardService;
    }

    public String createGame(String ownerEmail, GameLevel gameLevel) {
        String gameId = UUID.randomUUID().toString();
        String solution = generateWordleWord(gameLevel);
        Game game = Game.builder()
                .solution(solution)
                .id(gameId)
                .ownerEmail(ownerEmail)
                .level(gameLevel)
                .build();
        gameRedisTemplate.opsForValue().set(gameId, game, TTL);
        System.out.println("Created game with the solution " + solution);
        return gameId;
    }

    public String refreshGame(String ownerEmail, GameLevel gameLevel) {
        return createGame(ownerEmail, gameLevel);
    }

    public GameDTO loadGame(String gameId, String ownerEmail) {
        Game game = gameRedisTemplate.opsForValue().get(gameId);
        if (game == null) {
            throw new NoSuchElementException("Game with id " + gameId + " not found");
        }

        if (!game.getOwnerEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Owner email does not match");
        }
        return new GameDTO(game);
    }

    /**
     * Get the solution if the game is finished
     * @param gameId The game id
     * @param ownerEmail The onwer email
     * @return The solution of game
     */
    public String getGameSolution(String gameId, String ownerEmail) {
        Game game = gameRedisTemplate.opsForValue().get(gameId);
        if (game == null) {
            throw new NoSuchElementException("Game with id " + gameId + " not found");
        }
        if (!game.getOwnerEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Owner email does not match");
        }
        if (!game.isFinished()) {
            throw new IllegalArgumentException("Game is not finished");
        }
        return game.getSolution();
    }

    // Submit a guess and get the Guess Outcome from it
    public GuessOutcome submitGuess(String ownerEmail, String gameId, String rawGuess) {
        // Get the game by game id
        Game game = gameRedisTemplate.opsForValue().get(gameId);

        // Game cannot be null
        if (game == null) throw new NoSuchElementException("Game not found: " + gameId);

        // Ownership check
        if (!game.getOwnerEmail().equalsIgnoreCase(ownerEmail)) {
            throw new IllegalArgumentException("You do not own this game.");
        }

        String guess = Objects.requireNonNull(rawGuess).trim().toUpperCase();
        // Guess cannot include numbers or the length is not 5
        if (guess.length() != 5 || !guess.chars().allMatch(Character::isLetter)) {
            throw new IllegalArgumentException("Guess must be 5 letters A-Z");
        }

        if (!normalWordList.contains(guess.toLowerCase())) {
            throw new IllegalArgumentException("Guess is not a valid word");
        }

        if (game.isFinished()) {
            throw new IllegalArgumentException("Game is already finished: " + gameId);
        }

        // produce feedback with your judge function (two-pass: greens then yellows)
        List<Color> feedback = judge(gameId, guess);

        GuessOutcome guessOutcome = game.addGuessResult(guess, feedback);

        if (guessOutcome.correct()) {
            // delete the additional tries
            System.out.println("Game finished at: " + game.getFinishedAt());
            System.out.println("Correct guess: " + guessOutcome.toString());
            gameRepository.save(game);
            // Send the game to the leaderboard
            leaderboardService.recordCompletion(game.getOwnerEmail());
        }

        gameRedisTemplate.opsForValue().set(gameId, game, TTL);
        System.out.println("Submitted game with the solution " + guessOutcome.toString());
        return guessOutcome;
    }

    // Return the feedback of each try
    private List<Color> judge(String gameId, String rawGuess) {
        Game game = gameRedisTemplate.opsForValue().get(gameId);
        if (game == null) {
            throw new NoSuchElementException("Game not found: " + gameId);
        }
        if (rawGuess == null) {
            throw new IllegalArgumentException("guess is required");
        }

        String solution = game.getSolution().toUpperCase();
        String guess = rawGuess.trim().toUpperCase();

        if (guess.length() != 5 || !guess.chars().allMatch(Character::isLetter)) {
            throw new IllegalArgumentException("Guess must be exactly 5 letters A–Z");
        }

        Color[] out = new Color[5];           // result
        char[] sol = solution.toCharArray();  // solution letters
        char[] g = guess.toCharArray();     // guess letters
        boolean[] used = new boolean[5];      // which solution letters are consumed for YELLOW/GREEN

        // Pass 1: mark GREENS
        for (int i = 0; i < 5; i++) {
            if (g[i] == sol[i]) {
                out[i] = Color.GREEN;
                used[i] = true; // consume this solution position
            }
        }

        // Pass 2: for non-green positions, try to mark YELLOW if any unused matching letter remains
        for (int i = 0; i < 5; i++) {
            if (out[i] != null) continue; // already GREEN
            boolean found = false;
            for (int j = 0; j < 5; j++) {
                if (!used[j] && g[i] == sol[j]) {
                    out[i] = Color.YELLOW;
                    used[j] = true; // consume this solution letter
                    found = true;
                    break;
                }
            }
            if (!found) out[i] = Color.GREY;
        }

        return Arrays.asList(out); // List<Color> of size 5
    }

    // Load the word list from resources/WordList.json
    private List<String> loadWordList(ObjectMapper objectMapper, String fileName) {
        try (InputStream is = getClass().getResourceAsStream("/" + fileName)) {
            if (is == null) {
                throw new IllegalStateException(fileName + " not found in classpath (src/main/resources)");
            }

            List<String> wordList = objectMapper.readValue(is, new TypeReference<List<String>>() {});
            if (wordList.isEmpty()) {
                throw new IllegalStateException(fileName + " is empty");
            }

            return wordList;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fileName, e);
        }
    }

    // Generate a WordleWord
    private String generateWordleWord(GameLevel gameLevel) {
        Random random = new Random();
        if (gameLevel.equals(GameLevel.SIMPLE)) {
            return simpleWordList.get(random.nextInt(simpleWordList.size()));
        }
        return normalWordList.get(random.nextInt(normalWordList.size()));
    }

}
