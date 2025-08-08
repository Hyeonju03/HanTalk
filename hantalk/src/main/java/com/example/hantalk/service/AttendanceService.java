package com.example.hantalk.service;

import com.example.hantalk.entity.Attendance;
import com.example.hantalk.entity.Users;
import com.example.hantalk.repository.AttendanceRepository;
import com.example.hantalk.repository.UsersRepository; // UsersRepository 추가
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UsersRepository usersRepository; // UsersRepository 주입

    // 로그인 시 자동 출석 처리 (userNo를 인자로 받도록 수정)
    @Transactional
    public boolean checkAttendance(int userNo) {
        Users user = usersRepository.findByUserNo(userNo) // findByUserNo 메서드 인자도 int로 변경
                .orElseThrow(() -> new EntityNotFoundException("User not found with userNo: " + userNo));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        boolean alreadyChecked = attendanceRepository.existsByUsersAndAttendDateBetween(user, startOfDay, endOfDay);

        if (!alreadyChecked) {
            Attendance attendance = new Attendance();
            attendance.setUsers(user);
            attendance.setAttendDate(LocalDateTime.now());
            attendanceRepository.save(attendance);

            // 포인트 적립 로직
            user.setPoint(user.getPoint() + 10);
            usersRepository.save(user); // 변경된 사용자 정보 저장

            System.out.println(user.getUserId() + " 님이 " + LocalDate.now() + " 출석했습니다.");
            return true;
        } else {
            System.out.println(user.getUserId() + " 님은 이미 " + LocalDate.now() + " 출석했습니다.");
        }
        return false;
    }

    // 특정 사용자의 출석 날짜 리스트 조회 (LocalDate 타입)
    public List<LocalDate> getAttendanceDates(int userNo) { // Long -> int 로 변경
        Users user = usersRepository.findByUserNo(userNo) // findByUserNo 메서드 인자도 int로 변경
                .orElseThrow(() -> new EntityNotFoundException("User not found with userNo: " + userNo));

        return attendanceRepository.findByUsers_UserNo(userNo)
                .stream()
                .map(att -> att.getAttendDate().toLocalDate())
                .collect(Collectors.toList());
    }

    // 특정 사용자의 출석 날짜 리스트 조회 (String 타입) - 프론트엔드 연동용
    public List<String> getAttendanceDatesAsString(int userNo) { // Long -> int 로 변경
        Users user = usersRepository.findByUserNo(userNo) // findByUserNo 메서드 인자도 int로 변경
                .orElseThrow(() -> new EntityNotFoundException("User not found with userNo: " + userNo));

        return attendanceRepository.findByUsers_UserNo(userNo).stream()
                .map(att -> att.getAttendDate().toLocalDate().toString())
                .collect(Collectors.toList());
    }

    // 이 메서드는 int userNo를 사용하므로 변경 없이 유지
    public List<Attendance> findByUserNo(int userNo) {
        return attendanceRepository.findByUsers_UserNo(userNo);
    }
}