package com.example.hantalk.dto;

import com.example.hantalk.entity.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UsersDTO {
    private int user_no;
    private String user_id;
    private String name;
    private String email;
    private String password;
    private String nickname;
    private String profile_image;
    private LocalDateTime join_date;
    private LocalDateTime last_login;
    private LocalDateTime birth;
    private String status;
    private int point = 0;

    private List<Attendance> attendance_list;
    private List<Comment> comment_list;
    private List<Leaning_Log> leaning_log_list;
    private List<Post> post_list;
    private List<User_Items> user_items_list;
}
