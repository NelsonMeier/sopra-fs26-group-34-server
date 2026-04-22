package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs26.service.UserService;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final UserService userService;

    public LeaderboardController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{gameId}")
    public void setLeaderboard(@PathVariable String gameId,
                            @RequestBody Map<String, Integer> data) {
        userService.setLeaderboard(gameId, data);
    }

    @GetMapping("/{gameId}")
    public Map<String, Integer> getLeaderboard(@PathVariable String gameId) {
        return userService.getLeaderboard(gameId);
    }
}