package com.example.springboot_wordle.controller;


import com.example.springboot_wordle.dto.GameDTO;
import com.example.springboot_wordle.dto.UserDTO;
import com.example.springboot_wordle.model.User;
import com.example.springboot_wordle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
    public User getMyProfile(Authentication authentication) {
        return userService.getMyProfile(authentication);
    }

    @GetMapping("/me/game-activities")
    public List<GameDTO> getGameActivity(Authentication authentication,
                                         @RequestParam(defaultValue = "7") int days) {
        return userService.getGameActivity(authentication, days);
    }

    @GetMapping("/{username}")
    public UserDTO getOtherProfile(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{username}/game-activities")
    public List<GameDTO> getOtherGameActivity(@PathVariable String username,
                                         @RequestParam(defaultValue = "7") int days) {
        return userService.getGameActivityUsername(username, days);
    }


}
