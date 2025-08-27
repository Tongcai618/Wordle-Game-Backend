package com.example.springboot_wordle.repository;

import com.example.springboot_wordle.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.Instant;
import java.util.List;

public interface GameRepository extends MongoRepository<Game, String> {
    Game findByIdAndOwnerEmail(String id, String ownerEmail);
    List<Game> findByOwnerEmail(String ownerEmail);

    @Query("{ 'ownerEmail': ?0, 'finishedAt': { $gte: ?1 } }")
    List<Game> findByOwnerEmailAndFinishedAtAfter(String ownerEmail, Instant cutoff);
}

