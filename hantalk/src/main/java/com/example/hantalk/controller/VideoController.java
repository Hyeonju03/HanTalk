package com.example.hantalk.controller;

import com.example.hantalk.dto.*;
import com.example.hantalk.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VideoCreateDto dto) {
        Long id = videoService.createVideo(dto);
        return ResponseEntity.ok(id);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody VideoUpdateDto dto) {
        videoService.updateVideo(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideo(id));
    }

    @GetMapping
    public ResponseEntity<List<VideoResponseDto>> getAll() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok().build();
    }
}