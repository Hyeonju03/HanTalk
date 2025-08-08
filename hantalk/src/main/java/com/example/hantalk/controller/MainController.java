package com.example.hantalk.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo != null) {
            // 로그인 상태일 때
            model.addAttribute("isLoggedIn", true);
        } else {
            // 비로그인 상태일 때
            model.addAttribute("isLoggedIn", false);
        }
        return "main";
    }

}
