package com.example.hantalk.dto;


import com.example.hantalk.entity.User_Items;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemDTO {
    private int itemId; // 구분용
    private String itemName; // 상품명
    private String itemDescription; // 상품설명
    private String itemImage; // 상품이미지 (이미지 경로)
    private int price; // 가격
    private LocalDateTime createDate; // 등록일
    private LocalDateTime updateDate; // 수정일

    private List<User_Items> userItemsList;
}
