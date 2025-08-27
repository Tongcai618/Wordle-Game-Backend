package com.example.springboot_wordle.controller;


import com.example.springboot_wordle.dto.GameDTO;
import com.example.springboot_wordle.model.User;
import com.example.springboot_wordle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public User getMe(Authentication authentication) {
        return userService.getMyProfile(authentication);
    }

    @GetMapping("/me/game-activities")
    public List<GameDTO> getGameActivity(Authentication authentication,
                                         @RequestParam(defaultValue = "7") int days) {
        return userService.getGameActivity(authentication, days);
    }

}
