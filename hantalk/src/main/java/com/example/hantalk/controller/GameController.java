package com.example.hantalk.controller;

import com.example.hantalk.SessionUtil;
import com.example.hantalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
//        if (SessionUtil.isLoggedIn(session)) {
//            String userId = SessionUtil.getLoginUserId(session);
//            if (userId != null) {
//                model.addAttribute("userId", userId);
//            } else {
//                return "user/login";
//            }
//        } else {
//            return "user/login";
//        }

        // templates/game/game1.html 로 이동
        return "game/game1";
    }

//    @PutMapping("/api/users/updatePoints/{userId}")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> updatePoints(
//            @PathVariable("userId") String userId,
//            @RequestBody Map<String, Object> requestBody
//    ) {
//        int points = (int) requestBody.get("points");
//        System.out.println("User '" + userId + "' has updated their points to " + points);
//
//        // 실제 저장 로직 (예: userService.addPoints(userId, points));
//        // userService.addPoints(userId, points);
//
//        return ResponseEntity.ok(Map.of(
//                "status", "success",
//                "userId", userId,
//                "points", points,
//                "message", "Points updated successfully!"
//        ));
//    }
}
