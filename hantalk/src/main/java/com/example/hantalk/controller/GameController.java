package com.example.game.controller;

import com.example.hantalk.SessionUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Controller // 템플릿 반환을 위해 @Controller 사용
@RequestMapping("/game")
public class GameController {

    private final SessionUtil sessionUtil;

    // localhost:9001/game/game1 에 접속하면 game/game1.html을 반환합니다.
    @GetMapping("/game1")
    public String showGamePage(HttpSession session, Model model) {
        Integer userNo = SessionUtil.getLoginUserNo(session);

        if (userNo != null) {
            model.addAttribute("userNo", userNo);
            // userNo를 문자열로 변환하여 HTML에서 사용하기 용이하게 모델에 추가할 수도 있습니다.
            // model.addAttribute("userId", String.valueOf(userNo));
        } else {
            return "user/login";
        }

        return "game/game1"; // templates 폴더 기준의 상대 경로를 반환합니다.
    }

    /**
     * Unity에서 보낸 포인트를 업데이트하는 API 엔드포인트입니다.
     * WebGL 빌드에서 이 엔드포인트를 호출합니다.
     */
    @PutMapping("/api/users/updatePoints/{userId}")
    @ResponseBody // REST API 역할을 하는 메서드에 @ResponseBody 추가
    public ResponseEntity<String> updatePoints(@PathVariable("userId") String userId, @RequestBody Map<String, Object> requestBody) {
        // userId는 URL 경로에서 가져온 사용자 번호(userNo)입니다.
        // 이 값을 사용하여 데이터베이스를 업데이트하는 로직을 추가하세요.
        int points = (int) requestBody.get("points");
        System.out.println("User '" + userId + "' has updated their points to " + points);
        return ResponseEntity.ok("Points updated successfully!");
    }
}
