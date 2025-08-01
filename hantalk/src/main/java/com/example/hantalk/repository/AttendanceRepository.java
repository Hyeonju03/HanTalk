package com.example.hantalk.repository;

import com.example.hantalk.entity.Attendance;
import com.example.hantalk.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    boolean existsByUsersAndAttendDateBetween(Users users, LocalDateTime start, LocalDateTime end);

    List<Attendance> findByUsers(Users users);
}
