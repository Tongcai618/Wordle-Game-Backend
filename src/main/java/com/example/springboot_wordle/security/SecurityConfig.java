package com.example.springboot_wordle.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()       // disable CSRF for simplicity (ok for dev APIs)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // allow all endpoints
                )
                .httpBasic().disable()   // disable basic auth
                .formLogin().disable();  // disable login page
        return http.build();
    }
}
