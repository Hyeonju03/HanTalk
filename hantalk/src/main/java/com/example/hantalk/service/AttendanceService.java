package com.example.hantalk.service;

import com.example.hantalk.entity.Attendance;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    // 로그인 시 자동 출석 처리
    public boolean checkAttendance(Users users) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        boolean alreadyChecked = attendanceRepository.existsByUsersAndAttendDateBetween(users, startOfDay, endOfDay);

        if (!alreadyChecked) {
            Attendance attendance = new Attendance();
            attendance.setUsers(users);
            attendanceRepository.save(attendance);
            return true;
        }
        return false;
    }


    public List<LocalDate> getAttendanceDates(Users users) {
        return attendanceRepository.findByUsers(users)
                .stream()
                .map(att -> att.getAttendDate().toLocalDate())
                .collect(Collectors.toList());
    }

    public List<String> getAttendanceDatesAsString(Users user) {
        return attendanceRepository.findByUsers(user).stream()
                .map(att -> att.getAttendDate().toLocalDate().toString()) // "2025-07-31" 형식
                .collect(Collectors.toList());
    }

}
