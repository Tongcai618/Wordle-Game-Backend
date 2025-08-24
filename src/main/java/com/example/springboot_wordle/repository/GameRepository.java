package com.example.springboot_wordle.repository;

import com.example.springboot_wordle.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends MongoRepository<Game, String> {
    Optional<Game> findByIdAndOwnerEmail(String id, String ownerEmail);
    Optional<List<Game>> findByOwnerEmail(String ownerEmail);
}
