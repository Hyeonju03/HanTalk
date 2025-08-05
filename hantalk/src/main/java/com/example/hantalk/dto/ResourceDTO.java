package com.example.hantalk.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResourceDTO {
    private int resourceId;
    private String title;
    private String content;
    private String archive;
    private int viewCount;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String originalFileName;
    private boolean isPreviewAvailable;
    private String writer;

    public boolean isPreviewAvailable() {
        return isPreviewAvailable;
    }

    public void setPreviewAvailable(boolean previewAvailable) {
        this.isPreviewAvailable = previewAvailable;
    }
}
