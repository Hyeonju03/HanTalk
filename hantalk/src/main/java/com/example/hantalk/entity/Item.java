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
    @Column(name="item_id")
    private int itemId; // 구분용

    @Column(name="item_name", length = 200)
    private String itemName; // 상품명

    @Column(name="item_description", columnDefinition = "TEXT")
    private String itemDescription; // 상품설명

    @Column(name="item_image", length = 200)
    private String itemImage; // 상품이미지 (이미지 경로)

    private int price; // 가격

    @CreatedDate
    @Column(name="create_date")
    private LocalDateTime createDate; // 등록일

    @CreatedDate
    @Column(name="update_date")
    private LocalDateTime updateDate; // 수정일

    /// ////////////////////////////////////////

    @OneToMany(mappedBy = "item", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    @Column(name="user_items_list")
    private List<User_Items> userItemsList;
}
