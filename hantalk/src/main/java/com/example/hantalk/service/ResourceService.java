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

    // 실제 서버 파일 저장 경로 (컨트롤러와 동일)
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/ResourceFile";

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

    public ResourceDTO getResourceById(int id) {
        Optional<Resource> optional = resourceRepository.findById(id);
        return optional.map(this::toDTO).orElse(null);
    }

    public Page<ResourceDTO> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(this::toDTO);
    }

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

    public void deleteResource(int id) {
        resourceRepository.deleteById(id);
    }

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
        dto.setWriter(resource.getWriter());
        return dto;
    }

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
                // 뷰에서 사용할 경로는 /ResourceFile/파일명 으로 맞춤 (uploadDir 절대경로와 분리)
                resource.setArchive("/uploads/ResourceFile/" + savedFileName);
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

    public void updateResourceWithFile(int id, ResourceDTO dto, MultipartFile file) {
        resourceRepository.findById(id).ifPresent(resource -> {
            resource.setTitle(dto.getTitle());
            resource.setContent(dto.getContent());
            resource.setUpdateDate(LocalDateTime.now());

            if (file != null && !file.isEmpty()) {
                String savedFileName = saveFile(file);
                if (savedFileName != null) {
                    resource.setArchive("/uploads/ResourceFile/" + savedFileName);
                    resource.setOriginalFileName(file.getOriginalFilename());
                }
            } else {
                resource.setArchive(dto.getArchive());
                resource.setOriginalFileName(dto.getOriginalFileName());
            }

            resourceRepository.save(resource);
        });
    }

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

    // 💡💡💡 이 메서드를 수정했습니다. 💡💡💡
    // archive 경로를 온전히 구성해서 DB에서 조회하도록 변경
    public String getOriginalFileName(String storedFileName) {
        String fullPath = "/uploads/ResourceFile/" + storedFileName;
        return resourceRepository.findByArchive(fullPath)
                .map(Resource::getOriginalFileName)
                .orElse("다운로드파일.txt");
    }

    public Page<ResourceDTO> searchResources(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllResources(pageable);
        }
        String trimmedKeyword = keyword.trim();
        return resourceRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(trimmedKeyword, trimmedKeyword, pageable)
                .map(this::toDTO);
    }
}
