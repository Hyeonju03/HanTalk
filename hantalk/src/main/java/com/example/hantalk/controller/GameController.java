package com.example.hantalk.controller;

import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/game")
public class GameController {

    private final UserService userService;

    @GetMapping("/game1")
    public String showGamePage(HttpSession session, Model model) {
        return "game/game1";
    }

    @PutMapping("/api/users/updatePoints")
    public ResponseEntity<Map<String, Object>> updatePoints(
            @RequestBody Map<String, Object> requestBody,
            HttpSession session
    ) {
        // 세션에서 userId 가져오기
        String userId = (String) session.getAttribute("userId");

        // 세션에 userId가 없으면 인증 실패로 간주
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not logged in"));
        }

        int points;
        try {
            points = ((Number) requestBody.get("points")).intValue();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid points data"));
        }

        userService.addPoints(userId, points);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "userId", userId,
                "points", points,
                "message", "Points updated successfully!"
        ));
    }
}