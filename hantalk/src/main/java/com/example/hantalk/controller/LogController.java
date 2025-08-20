package com.example.hantalk.controller;

import com.example.hantalk.dto.LogDataDTO;
import com.example.hantalk.service.LogService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @GetMapping("/admin/getLogdata")
    public String getAllLog(Model model) {
        List<LogDataDTO> logList = logService.getLog();
        model.addAttribute("list", logList);
        return "logPage/logList";
    }

    @GetMapping("/user/{userNo}")
    public String getLogsByUser(@PathVariable int userNo, Model model) {
        List<LogDataDTO> logList = logService.getLogToUser(userNo);
        model.addAttribute("list", logList);
        model.addAttribute("filter",  userNo);
        return "logPage/logList";
    }
    @GetMapping("/date")
    public String getLogsByDate(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                Model model) {
        List<LogDataDTO> logList = logService.getLogToDate(startDate, endDate);
        model.addAttribute("list", logList);
        model.addAttribute("filter", "기간: " + startDate + " ~ " + endDate);
        return "logPage/logList";
    }
    @GetMapping("/user-date")
    public String getLogsByUserAndDate(@RequestParam("userNo") int userNo,
                                       @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                       @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                       Model model) {
        List<LogDataDTO> logList = logService.getLogToUserAndDate(userNo, startDate, endDate);
        model.addAttribute("list", logList);
        model.addAttribute("filter", "사용자: " + userNo + ", 기간: " + startDate + " ~ " + endDate);
        return "logPage/logList";
    }

}