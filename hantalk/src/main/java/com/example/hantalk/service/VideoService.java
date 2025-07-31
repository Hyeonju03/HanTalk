package com.example.hantalk.service;

import com.example.hantalk.dto.VideoDTO;
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
    public int createVideo(VideoDTO dto) {
        Video video = toEntity(dto);
        return videoRepository.save(video).getVideoId();
    }

    @Transactional
    public void updateVideo(VideoDTO dto) {
        Video video = videoRepository.findById(dto.getVideoId())
                .orElseThrow(() -> new IllegalArgumentException("영상이 존재하지 않습니다."));
        video.setTitle(dto.getTitle());
        video.setContent(dto.getContent());
        video.setVideoName(dto.getVideoName());
    }

    @Transactional(readOnly = true)
    public VideoDTO getVideo(int id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("영상이 존재하지 않습니다."));
        return toDto(video);
    }

    @Transactional(readOnly = true)
    public List<VideoDTO> getAllVideos() {
        return videoRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteVideo(int id) {
        videoRepository.deleteById(id);
    }

    // 변환 메서드들
    private VideoDTO toDto(Video video) {
        VideoDTO dto = new VideoDTO();
        dto.setVideoId(video.getVideoId());
        dto.setTitle(video.getTitle());
        dto.setContent(video.getContent());
        dto.setVideoName(video.getVideoName());
        dto.setCreateDate(video.getCreateDate());
        dto.setUpdateDate(video.getUpdateDate());

        return dto;
    }

    private Video toEntity(VideoDTO dto) {
        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setContent(dto.getContent());
        video.setVideoName(dto.getVideoName());
        return video;
    }
}