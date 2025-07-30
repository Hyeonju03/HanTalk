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
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int item_id; // 구분용

    @Column(length = 200)
    private String item_name; // 상품명

    @Column(columnDefinition = "TEXT")
    private String item_description; // 상품설명

    @Column(length = 200)
    private String item_image; // 상품이미지 (이미지 경로)

    private int price; // 가격

    @CreatedDate
    private LocalDateTime create_date; // 등록일

    @CreatedDate
    private LocalDateTime update_date; // 수정일

    /// ////////////////////////////////////////

    @OneToMany(mappedBy = "item", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<User_Items> user_items_list;
}
