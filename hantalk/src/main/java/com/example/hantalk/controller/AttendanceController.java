package com.example.hantalk.controller;

import com.example.hantalk.entity.Users;
import com.example.hantalk.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 로그인 시 자동 출석 처리 (예: 로그인 성공 후 호출)
    @GetMapping("/check")
    public String checkAttendance(HttpSession session) {
        Users loginUser = (Users) session.getAttribute("loginUser");
        if (loginUser != null) {
            attendanceService.checkAttendance(loginUser);
        }
        return "redirect:/attendance/calendar";  // 출석 현황 페이지로 이동
    }

    // 출석 현황 캘린더 조회
    @GetMapping("/calendar")
    public String viewAttendanceCalendar(HttpSession session, Model model) {
        Users loginUser = (Users) session.getAttribute("loginUser");
        if (loginUser != null) {
            List<LocalDate> attendanceDates = attendanceService.getAttendanceDates(loginUser);
            model.addAttribute("attendanceDates", attendanceDates);
        }
        return "user/calendar"; // Thymeleaf 뷰 이름
    }

    @GetMapping("/calendar/data")
    @ResponseBody
    public List<String> getAttendanceDates(HttpSession session) {
        Users user = (Users) session.getAttribute("loginUser");
        return attendanceService.getAttendanceDatesAsString(user);
    }

}
