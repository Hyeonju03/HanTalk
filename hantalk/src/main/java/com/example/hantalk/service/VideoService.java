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
        return videoRepository.save(video).getVideo_id();
    }

    @Transactional
    public void updateVideo(VideoDTO dto) {
        Video video = videoRepository.findById(dto.getVideo_id())
                .orElseThrow(() -> new IllegalArgumentException("영상이 존재하지 않습니다."));
        video.setTitle(dto.getTitle());
        video.setContent(dto.getContent());
        video.setVideo_name(dto.getVideo_name());
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
        dto.setVideo_id(video.getVideo_id());
        dto.setTitle(video.getTitle());
        dto.setContent(video.getContent());
        dto.setVideo_name(video.getVideo_name());
        dto.setCreate_date(video.getCreate_date());
        dto.setUpdate_date(video.getUpdate_date();

        return dto;
    }

    private Video toEntity(VideoDTO dto) {
        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setContent(dto.getContent());
        video.setVideo_name(dto.getVideo_name());
        return video;
    }
}