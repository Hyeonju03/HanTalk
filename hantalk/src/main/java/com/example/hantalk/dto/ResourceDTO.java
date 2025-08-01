package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class ResourceDTO {
    private Long resource_id;
    private String title;
    private String content;
    private String archive;
    private int view_count;
    private LocalDateTime create_date;
    private LocalDateTime update_date;

    public void setCreateDate(LocalDateTime now) {
        this.create_date = now;
    }
}
