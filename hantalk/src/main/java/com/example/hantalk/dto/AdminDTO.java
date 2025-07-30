package com.example.hantalk.dto;

import com.example.hantalk.entity.Users;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminDTO {
    private int admin_no;
    private String admin_id;
    private String email;
    private String password;
    private String nickname;
}
