package com.example.hantalk.service;

import com.example.hantalk.dto.*;
import com.example.hantalk.entity.Video;
import com.example.hantalk.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Transactional
    public Long createVideo(VideoCreateDTO dto) {
        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setContent(dto.getContent());
        video.setVideoName(dto.getVideoName());
        return videoRepository.save(video).getId();
    }

    @Transactional
    public void updateVideo(VideoUpdateDTO dto) {
        Video video = videoRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("영상이 존재하지 않습니다."));
        video.setTitle(dto.getTitle());
        video.setContent(dto.getContent());
        video.setVideoName(dto.getVideoName());
    }

    @Transactional(readOnly = true)
    public VideoResponseDTO getVideo(Long id) {
        return videoRepository.findById(id)
                .map(VideoResponseDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("영상이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<VideoResponseDTO> getAllVideos() {
        return videoRepository.findAll()
                .stream()
                .map(VideoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }
}