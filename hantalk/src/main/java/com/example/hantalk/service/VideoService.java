package com.example.hantalk.service;

import com.example.hantalk.dto.VideoDTO;
import com.example.hantalk.entity.Video;
import com.example.hantalk.repository.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public boolean existsByFilename(String filename) {
        return videoRepository.existsByVideoName(filename);
    }

    // 🔍 제목으로 영상 검색
    @Transactional(readOnly = true)
    public List<VideoDTO> searchByTitle(String keyword) {
        return videoRepository.findByTitleContaining(keyword)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 🔍 내용으로 영상 검색
    @Transactional(readOnly = true)
    public List<VideoDTO> searchByContent(String keyword) {
        return videoRepository.findByContentContaining(keyword)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 🔍 제목 또는 내용으로 영상 검색
    @Transactional(readOnly = true)
    public List<VideoDTO> searchByTitleOrContent(String keyword) {
        return videoRepository.findByTitleContainingOrContentContaining(keyword, keyword)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ 영상 등록
    @Transactional
    public int createVideo(VideoDTO dto) {
        Video video = toEntity(dto);
        return videoRepository.save(video).getVideoId();
    }

    // ✅ 영상 수정
    @Transactional
    public void updateVideo(VideoDTO dto) {
        Video video = videoRepository.findById(dto.getVideoId())
                .orElseThrow(() -> new IllegalArgumentException("영상이 존재하지 않습니다."));
        video.setTitle(dto.getTitle());
        video.setContent(dto.getContent());
        video.setVideoName(dto.getVideoName());
    }

    // ✅ 단일 영상 조회
    @Transactional(readOnly = true)
    public VideoDTO getVideo(int id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("영상이 존재하지 않습니다."));
        return toDto(video);
    }

    // ✅ 전체 영상 목록 조회
    @Transactional(readOnly = true)
    public List<VideoDTO> getAllVideos() {
        return videoRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ 영상 삭제
    @Transactional
    public void deleteVideo(int id) {
        videoRepository.deleteById(id);
    }

    // ⚙️ Entity → DTO 변환
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

    // ⚙️ DTO → Entity 변환
    private Video toEntity(VideoDTO dto) {
        Video video = new Video();
        video.setVideoId(dto.getVideoId()); // 수정 시 필요
        video.setTitle(dto.getTitle());
        video.setContent(dto.getContent());
        video.setVideoName(dto.getVideoName());
        return video;
    }

    public Page<Video> getPagedVideos(String keyword, String searchType, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case "title":
                    return videoRepository.findByTitleContaining(keyword, pageable);
                case "content":
                    return videoRepository.findByContentContaining(keyword, pageable);
                case "filename":
                    return videoRepository.findByVideoNameContaining(keyword, pageable);
                case "all":
                default:
                    return videoRepository.findByTitleOrContentOrVideoNameContaining(keyword, pageable);
            }
        } else {
            return videoRepository.findAll(pageable);
        }
    }

}
