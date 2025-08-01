package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})
@Entity
@Getter
@Setter
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="attendance_id")
    private int attendanceId;

    @CreatedDate
    @Column(name="attend_date", nullable = false)
    private LocalDateTime attendDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Users users;
}
