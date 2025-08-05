package com.example.hantalk.controller;

import com.example.hantalk.dto.UsersDTO;
import com.example.hantalk.entity.User_Items;
import com.example.hantalk.service.Learning_LogService;
import com.example.hantalk.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final Learning_LogService learningLogService;

    // 마이페이지 조회
    @GetMapping("/view")
    public String myPage(Model model, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        UsersDTO dto = myPageService.getMyPageInfo(userNo);
        model.addAttribute("user", dto);

        // 구매한 아이템 목록 추가
        List<User_Items> userItems = myPageService.getUserOwnedItems(userNo);
        model.addAttribute("userItems", userItems);

        return "user/view";
    }

    // 통계 페이지 조회
    @GetMapping("/statistics")
    public String showStatisticsPage(Model model, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";
        }

        // --- 여기에서 통계 데이터를 조회하여 Model에 담습니다 ---
        // (기간은 예시로 현재 날짜로부터 30일 전까지로 설정)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        // 1. 총 학습량 조회
        int totalLearningCount = learningLogService.calculateTotalLearningCount(userNo);
        model.addAttribute("totalLearningCount", totalLearningCount);

        // 2. 일별 학습량 조회 (Map 또는 DTO 리스트)
        // DTO를 사용하지 않기로 하셨으니 Map으로 받습니다.
        Map<LocalDate, Long> dailyLearningStats = learningLogService.getDailyLearningStatsForPeriod(userNo, startDateTime, endDateTime);
        model.addAttribute("dailyLearningStats", dailyLearningStats);

        // --- -------------------------------------------- ---

        // Thymeleaf 템플릿 파일 이름 (예: templates/user/statistics.html)
        return "user/statistics";
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

    @PostMapping("/apply-profile")
    public String applyProfileImage(@RequestParam("itemId") int itemId, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) return "redirect:/user/login";

        myPageService.applyProfileImage(userNo, itemId);
        return "redirect:/user/view";
    }

    @GetMapping("/items")
    public String userItems(Model model, HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) return "redirect:/user/login";

        List<User_Items> userItems = myPageService.getUserItems(userNo);
        model.addAttribute("userItems", userItems);
        return "user/items";
    }
}
