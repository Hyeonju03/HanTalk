package com.example.hantalk.controller;

import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 조회
    @GetMapping("/view")
    public String myPage(Model model, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        UsersDTO dto = myPageService.getMyPageInfo(userNo);
        model.addAttribute("user", dto);
        return "user/view";
    }

    @GetMapping("/update")
    public String update(Model model, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";  // 로그인 안 되어 있으면 로그인 페이지로 이동
        }

        UsersDTO dto = myPageService.getMyPageInfo(userNo);
        model.addAttribute("user", dto);

        return "user/update";  // 수정 폼 페이지 이름 (templates/user/updateForm.html)
    }


    // 마이페이지 수정
    @PostMapping("/update")
    public String updateProc(@ModelAttribute UsersDTO dto, HttpSession session) {
        dto.setUserNo((Integer) session.getAttribute("userNo"));
        myPageService.updateMyPage(dto);
        return "redirect:/user/view";
    }

    @GetMapping("/delete")
    public String delete(Model model, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";  // 로그인 안 되어 있으면 로그인 페이지로 이동
        }
        return "user/delete";
    }
    // 탈퇴
    @PostMapping("/delete")
    public String deleteProc(HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        myPageService.deactivateUser(userNo);
        session.invalidate(); // 세션 만료
        return "redirect:/user/login"; // 로그인페이지로
    }

}
