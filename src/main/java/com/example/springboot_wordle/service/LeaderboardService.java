package com.example.springboot_wordle.service;

import com.example.springboot_wordle.model.User;
import com.example.springboot_wordle.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class LeaderboardService {

    private static final String EVENTS_ZSET = "lb:events"; // member: userId:uuid  score: epochMillis
    private static final String ROLLING_ZSET = "lb:24h";    // member: userId       score: count
    private static final long WINDOW_MS = 24L * 60 * 60 * 1000;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    public LeaderboardService(RedisTemplate<String, Object> redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    // Call when a user wins a game
    public void recordCompletion(String userEmail) {
        long now = Instant.now().toEpochMilli();
        String member = userEmail + ":" + UUID.randomUUID();

        System.out.println("record completion: " + member);

        // Store timed event
        redisTemplate.opsForZSet().add(EVENTS_ZSET, member, now);

        // Increment rolling count (+1)
        redisTemplate.opsForZSet().incrementScore(ROLLING_ZSET, userEmail, 1);
    }

    // Clean up expired events (every 600 seconds = 10 minutes)
    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void cleanupOldEvents() {
        long cutoff = Instant.now().toEpochMilli() - WINDOW_MS;

        Set<ZSetOperations.TypedTuple<Object>> oldEntries =
                redisTemplate.opsForZSet().rangeByScoreWithScores(EVENTS_ZSET, 0, cutoff);
        if (oldEntries == null) return;

        for (ZSetOperations.TypedTuple<Object> oldEntry : oldEntries) {
            String member = (String) oldEntry.getValue();
            if (member == null || !member.contains(":")) continue;

            String userId = member.substring(0, member.indexOf(':'));

            // decrement rolling score
            redisTemplate.opsForZSet().incrementScore(ROLLING_ZSET, userId, -1);
            // remove expired event
            redisTemplate.opsForZSet().remove(EVENTS_ZSET, member);
        }
    }

    // Refresh cached leaderboard (every 1 minute)
    @Scheduled(fixedRate = 1000 * 60) // every 60 seconds
    public void refreshTopLeaderboardCache() {
        Map<String, Integer> latest = buildTopUsersMap();
        redisTemplate.opsForValue().set("cached:leaderboard", latest);
        System.out.println("Top leaderboard cache refreshed: " + latest);
    }

    // Read cached leaderboard safely
    public Map<String, Integer> getCachedLeaderboard() {
        Object obj = redisTemplate.opsForValue().get("cached:leaderboard");
        if (!(obj instanceof Map<?, ?> map)) return Map.of();

        // preserve order
        Map<String, Integer> out = new LinkedHashMap<>();
        map.forEach((k, v) -> out.put((String) k, v == null ? 0 : ((Number) v).intValue()));
        return out;
    }

    // Build top-k directly from the ZSET (members are userIds)
    private Map<String, Integer> buildTopUsersMap() {
        Set<ZSetOperations.TypedTuple<Object>> set =
                redisTemplate.opsForZSet().reverseRangeWithScores(ROLLING_ZSET, 0, 20 - 1);

        Map<String, Integer> result = new LinkedHashMap<>();
        if (set == null) return result;

        for (ZSetOperations.TypedTuple<Object> tuple : set) {
            String userEmail = (String) tuple.getValue(); // already user email
            if (userEmail == null) continue;
            // Convert the email to the user name
            String userUsername = userRepository.findByEmail(userEmail)
                    .map(User::getUsername)
                    .orElse(userEmail);
            int score = tuple.getScore() == null ? 0 : (int) Math.round(tuple.getScore());
            result.put(userUsername, score);
        }
        return result;
    }
}
