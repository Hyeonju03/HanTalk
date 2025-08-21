package com.example.hantalk.controller;

import com.example.hantalk.dto.PostDTO;
import com.example.hantalk.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    // DB에 있는 CATEGORY 테이블의 ID를 상수로 정의
    private static final int NOTICE_CATEGORY_ID = 1;
    private static final int COMMUNITY_CATEGORY_ID = 2;

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

    @GetMapping("/hantalk/home")
    public String hantalkHome(HttpSession session, Model model) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo != null) {
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        // 최신 공지사항 3개를 가져와 모델에 추가
        List<PostDTO> notices = postService.getLatestPosts(NOTICE_CATEGORY_ID, 3);
        model.addAttribute("notices", notices);

        // 최신 커뮤니티 게시물 3개를 가져와 모델에 추가
        List<PostDTO> communityPosts = postService.getLatestPosts(COMMUNITY_CATEGORY_ID, 3);
        model.addAttribute("communityPosts", communityPosts);

        return "hantalk/home";
    }
}
