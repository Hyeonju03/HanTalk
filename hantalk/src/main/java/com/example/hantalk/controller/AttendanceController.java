package com.example.hantalk.controller;

import com.example.hantalk.entity.Users; // 이 임포트는 엔티티 클래스 정의를 위해 유지
import com.example.hantalk.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/check")
    public String checkAttendance(HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo"); // 세션에서 Integer로 가져옴

        if (userNo != null) {
            attendanceService.checkAttendance(userNo.intValue()); // int 타입으로 변환하여 서비스에 전달
        }
        return "redirect:/attendance/calendar";
    }

    @GetMapping("/calendar")
    public String viewAttendanceCalendar(HttpSession session, Model model) {
        Integer userNo = (Integer) session.getAttribute("userNo"); // 세션에서 Integer로 가져옴

        if (userNo != null) {
            List<LocalDate> attendanceDates = attendanceService.getAttendanceDates(userNo.intValue());
            model.addAttribute("attendanceDates", attendanceDates);
        }
        return "user/calendar";
    }

    @GetMapping("/calendar/data")
    @ResponseBody
    public ResponseEntity<List<String>> getAttendanceDatesJson(HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo"); // 세션에서 Integer로 가져옴

        if (userNo == null) {
            return ResponseEntity.ok(List.of());
        }

        // checkAttendance 로직은 필요에 따라 여기에 두거나 로그인 시에만 실행하도록 조정
        attendanceService.checkAttendance(userNo.intValue());

        List<String> dateList = attendanceService.getAttendanceDatesAsString(userNo.intValue());
        return ResponseEntity.ok(dateList);
    }
}