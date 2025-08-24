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

    // ✅ 전체 영상 목록 조회 (페이징 및 검색 기능 포함)
    @Transactional(readOnly = true)
    public Page<VideoDTO> getPagedVideos(String keyword, String searchType, Pageable pageable) {
        // 검색어 및 검색 유형에 따라 동적으로 쿼리 실행
        Page<Video> videoPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = keyword.trim();
            switch (searchType) {
                case "title":
                    videoPage = videoRepository.findByTitleContaining(keyword, pageable);
                    break;
                case "content":
                    videoPage = videoRepository.findByContentContaining(keyword, pageable);
                    break;
                case "filename":
                    videoPage = videoRepository.findByVideoNameContaining(keyword, pageable);
                    break;
                case "all":
                default:
                    // VideoRepository의 @Query 메서드와 일치하도록 수정
                    videoPage = videoRepository.findByTitleOrContentOrVideoNameContaining(keyword, pageable);
                    break;
            }
        } else {
            // 키워드가 없으면 전체 목록 조회
            videoPage = videoRepository.findAll(pageable);
        }

        // Entity Page를 DTO Page로 변환
        return videoPage.map(this::toDto);
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

    public boolean existsByVideoName(String filename) {
        return videoRepository.existsByVideoName(filename);
    }
}
