package com.example.hantalk.dto;


import com.example.hantalk.entity.User_Items;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemDTO {
    private int item_id; // 구분용
    private String item_name; // 상품명
    private String item_description; // 상품설명
    private String item_image; // 상품이미지 (이미지 경로)
    private int price; // 가격
    private LocalDateTime create_date; // 등록일
    private LocalDateTime update_date; // 수정일

    private List<User_Items> user_items_list;
}
