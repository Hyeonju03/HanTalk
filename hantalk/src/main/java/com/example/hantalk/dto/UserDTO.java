package com.example.hantalk.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDTO {
    private int userNo;
    private String userId;
    private String name;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;
    private LocalDateTime joinDate;
    private LocalDateTime birth;
    private String status;
    private int point = 0;
}
