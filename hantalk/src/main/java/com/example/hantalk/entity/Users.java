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
    private int user_no;

    @Column(nullable = false, unique = true)
    private String user_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(length = 200)
    private String profile_image;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime join_date;
    
    //값 직접 입력 필요
    private LocalDateTime last_login;

    @Column(nullable = false)
    private int birth;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private int point = 0;

    //////////////////////////////////////////////////

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Attendance> attendance_list;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Comment> comment_list;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Leaning_Log> leaning_log_list;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Post> post_list;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<User_Items> user_items_list;
}
