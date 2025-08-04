package com.example.hantalk.service;

import com.example.hantalk.dto.ResourceDTO;
import com.example.hantalk.entity.Resource;
import com.example.hantalk.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    // 업로드할 파일을 저장할 서버 내 디렉터리 경로
    private final String uploadDir = "C:\\aaa\\HanTalk\\hantalk\\upload";

    // 파일 업로드 없이 자료 등록
    public void createResource(ResourceDTO dto) {
        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setContent(dto.getContent());
        resource.setArchive(dto.getArchive());
        resource.setOriginalFileName(dto.getOriginalFileName());
        resource.setViewCount(0);
        resource.setCreateDate(LocalDateTime.now());
        resource.setUpdateDate(LocalDateTime.now());
        resourceRepository.save(resource);
    }

    // ID로 자료 조회
    public ResourceDTO getResourceById(int id) {
        Optional<Resource> optional = resourceRepository.findById(id);
        return optional.map(this::toDTO).orElse(null);
    }

    // 전체 목록 페이직 조회
    public Page<ResourceDTO> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(this::toDTO);
    }

    // 파일 업로드 없이 자료 수정
    public void updateResource(int id, ResourceDTO dto) {
        resourceRepository.findById(id).ifPresent(resource -> {
            resource.setTitle(dto.getTitle());
            resource.setContent(dto.getContent());
            resource.setArchive(dto.getArchive());
            resource.setOriginalFileName(dto.getOriginalFileName());
            resource.setUpdateDate(LocalDateTime.now());
            resourceRepository.save(resource);
        });
    }

    // 자료 삭제
    public void deleteResource(int id) {
        resourceRepository.deleteById(id);
    }

    // Entity → DTO 변환
    private ResourceDTO toDTO(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setResourceId(resource.getResourceId());
        dto.setTitle(resource.getTitle());
        dto.setContent(resource.getContent());
        dto.setArchive(resource.getArchive());
        dto.setOriginalFileName(resource.getOriginalFileName());
        dto.setViewCount(resource.getViewCount());
        dto.setCreateDate(resource.getCreateDate());
        dto.setUpdateDate(resource.getUpdateDate());
        dto.setOriginalFileName(resource.getOriginalFileName());
        return dto;
    }

    // 파일 포함 자료 등록
    public void createResourceWithFile(ResourceDTO dto, MultipartFile file) {
        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setContent(dto.getContent());
        resource.setViewCount(0);
        resource.setCreateDate(LocalDateTime.now());
        resource.setUpdateDate(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            String savedFileName = saveFile(file);
            if (savedFileName != null) {
                resource.setArchive("/upload/" + savedFileName);
                resource.setOriginalFileName(file.getOriginalFilename());
            } else {
                resource.setArchive(null);
                resource.setOriginalFileName(null);
            }
        } else {
            resource.setArchive(dto.getArchive());
            resource.setOriginalFileName(dto.getOriginalFileName());
        }

        resourceRepository.save(resource);
    }

    // 파일 포함 자료 수정
    public void updateResourceWithFile(int id, ResourceDTO dto, MultipartFile file) {
        resourceRepository.findById(id).ifPresent(resource -> {
            resource.setTitle(dto.getTitle());
            resource.setContent(dto.getContent());
            resource.setUpdateDate(LocalDateTime.now());

            if (file != null && !file.isEmpty()) {
                String savedFileName = saveFile(file);
                if (savedFileName != null) {
                    resource.setArchive("/upload/" + savedFileName);
                    resource.setOriginalFileName(file.getOriginalFilename());
                }
            } else {
                resource.setArchive(dto.getArchive());
                resource.setOriginalFileName(dto.getOriginalFileName());
            }

            resourceRepository.save(resource);
        });
    }

    // 실제 파일 저장 메서드 (UUID)
    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String ext = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String savedFileName = UUID.randomUUID().toString() + ext;
            Path filePath = uploadPath.resolve(savedFileName);
            file.transferTo(filePath.toFile());

            return savedFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 저장된 파일명으로 originalFileName 조회
    public String getOriginalFileName(String storedFileName) {
        return resourceRepository.findByArchiveEndingWith(storedFileName)
                .map(Resource::getOriginalFileName)
                .orElse("다운로드파일.txt");
    }

    public Page<ResourceDTO> searchResources(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // 검색어가 없으면 전체 조회
            return getAllResources(pageable);
        }
        String trimmedKeyword = keyword.trim();
        return resourceRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(trimmedKeyword, trimmedKeyword, pageable)
                .map(this::toDTO);
    }
}
