package com.example.hantalk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@EntityListeners(value = {AuditingEntityListener.class})
@Entity
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_no")
    private int userNo;

    @Column(name="user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name="profile_image", length = 200)
    private String profileImage;

    @Column(name = "profile_frame", length = 200)
    private String profileFrame;

    @CreatedDate
    @Column(name="join_date", nullable = false)
    private LocalDateTime joinDate;
    
    //값 직접 입력 필요
    @Column(name="last_login", nullable = false)
    private LocalDateTime lastLogin;

    @Column(nullable = false)
    private int birth;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private int point = 0;

    //////////////////////////////////////////////////

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Attendance> attendanceList;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Comment> commentList;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Learning_Log> learningLogList;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Post> postList;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<User_Items> userItemsList;
}
