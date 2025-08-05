package com.example.hantalk.dto;

import com.example.hantalk.entity.Item;
import com.example.hantalk.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class User_ItemsDTO {
    private int userItemId; // 구분용
    private LocalDateTime acquiredAt; // 획득일
    private Boolean equipped = false; // 착용 여부 (기본값: false)

    private Users users;
    private Item item;
}
