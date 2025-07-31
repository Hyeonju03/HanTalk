package com.example.hantalk.dto;

import com.example.hantalk.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UsersDTO {
    private int userNo;
    private String userId;
    private String name;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;
    private LocalDateTime joinDate;
    private LocalDateTime lastLogin;
    private int birth;
    private String status;
    private int point = 0;

    private List<Attendance> attendanceList;
    private List<Comment> commentList;
    private List<Leaning_Log> leaningLogList;
    private List<Post> postList;
    private List<User_Items> userItemsList;
}
