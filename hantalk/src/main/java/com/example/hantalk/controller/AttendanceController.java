package com.example.hantalk.controller;

import com.example.hantalk.entity.Users; // 이 임포트는 엔티티 클래스 정의를 위해 유지
import com.example.hantalk.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // ✅ PostMapping 임포트 추가
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap; // ✅ HashMap 임포트 추가
import java.util.List;
import java.util.Map; // ✅ Map 임포트 추가

import org.slf4j.Logger; // ✅ Logger 임포트 추가
import org.slf4j.LoggerFactory; // ✅ LoggerFactory 임포트 추가


@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class AttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class); // Logger 인스턴스 생성
    private final AttendanceService attendanceService;

    @GetMapping("/calendar")
    public String viewAttendanceCalendar(HttpSession session, Model model) {
        Integer userNo = (Integer) session.getAttribute("userNo"); // 세션에서 Integer로 가져옴

        if (userNo != null) {
            // 여기서는 캘린더 뷰를 단순히 로드하며, 데이터는 JS에서 /calendar/data API로 가져옵니다.
            logger.info("userNo {} 님의 출석 캘린더 페이지 요청", userNo);
        } else {
            logger.warn("로그인되지 않은 사용자가 캘린더 페이지에 접근 시도");
            return "redirect:/user/login"; // 로그인 페이지로 리다이렉트
        }
        return "user/calendar"; // HTML 파일 경로
    }

    @GetMapping("/calendar/data")
    @ResponseBody
    public ResponseEntity<List<String>> getAttendanceDatesJson(HttpSession session) {
        Integer userNo = (Integer) session.getAttribute("userNo"); // 세션에서 Integer로 가져옴
        logger.info("GET /user/calendar/data 호출 - userNo: {}", userNo);

        if (userNo == null) {
            logger.warn("세션에 userNo가 없어 출석 데이터를 반환할 수 없습니다.");
            return ResponseEntity.ok(List.of()); // 빈 리스트 반환
        }

        List<String> dateList = attendanceService.getAttendanceDatesAsString(userNo.intValue());
        logger.info("userNo {} 님의 출석 날짜 데이터 조회 완료: {}", userNo, dateList);
        return ResponseEntity.ok(dateList);
    }

    // "오늘 출석하기" 버튼의 POST 요청을 처리하는 새로운 엔드포인트
    @PostMapping("/checkTodayAttendance")
    @ResponseBody // JSON 응답을 반환
    public ResponseEntity<Map<String, Object>> checkTodayAttendance(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userNo = (Integer) session.getAttribute("userNo");
        logger.info("POST /user/checkTodayAttendance 호출 - userNo: {}", userNo);

        if (userNo == null) {
            logger.warn("로그인되지 않은 사용자가 출석 체크 시도");
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response); // HTTP 401 Unauthorized
        }

        try {
            boolean result = attendanceService.checkAttendance(userNo.intValue()); // 출석 서비스 호출

            if (result) {
                response.put("success", true);
                response.put("message", "출석 완료! 10 포인트가 적립되었습니다.");
                logger.info("userNo {} 님의 오늘 출석 성공 및 10 포인트 적립.", userNo);
            } else {
                response.put("success", false);
                response.put("message", "이미 오늘 출석했거나 출석에 실패했습니다.");
                logger.warn("userNo {} 님의 오늘 출석 실패 (이미 출석했거나 다른 문제 발생).", userNo);
            }
        } catch (Exception e) {
            logger.error("userNo {} 의 출석 체크 중 예외 발생: {}", userNo, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "서버 오류로 출석에 실패했습니다.");
        }
        return ResponseEntity.ok(response);
    }
}