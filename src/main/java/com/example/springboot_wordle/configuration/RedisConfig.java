package com.example.springboot_wordle.configuration;

import com.example.springboot_wordle.model.Game;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Game> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Game> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer()); // keys as strings
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Game.class)); // serialize Game as JSON
        return template;
    }
}