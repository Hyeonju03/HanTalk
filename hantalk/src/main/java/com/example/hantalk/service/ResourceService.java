package com.example.hantalk.service;

import com.example.hantalk.dto.ResourceDTO;
import com.example.hantalk.entity.Resource;
import com.example.hantalk.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    // 📌 자료 등록
    public void createResource(ResourceDTO dto) {
        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setContent(dto.getContent());
        resource.setArchive(dto.getArchive());
        resource.setViewCount(0); // 신규 등록 시 조회수 0
        resource.setCreateDate(LocalDateTime.now());
        resource.setUpdateDate(LocalDateTime.now());
        resourceRepository.save(resource);
    }

    // 📌 ID로 자료 조회
    public ResourceDTO getResourceById(int id) {
        Optional<Resource> optional = resourceRepository.findById((long) id);
        return optional.map(this::toDTO).orElse(null);
    }

    // 📌 전체 목록 페이지네이션 조회
    public Page<ResourceDTO> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(this::toDTO);
    }

    // 📌 자료 수정
    public void updateResource(int id, ResourceDTO dto) {
        resourceRepository.findById((long) id).ifPresent(resource -> {
            resource.setTitle(dto.getTitle());
            resource.setContent(dto.getContent());
            resource.setArchive(dto.getArchive());
            resource.setUpdateDate(LocalDateTime.now());
            resourceRepository.save(resource);
        });
    }

    // 📌 자료 삭제
    public void deleteResource(int id) {
        resourceRepository.deleteById((long) id);
    }

    // 📌 Entity → DTO 변환
    private ResourceDTO toDTO(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setResourceId((long) resource.getResourceId());
        dto.setTitle(resource.getTitle());
        dto.setContent(resource.getContent());
        dto.setArchive(resource.getArchive());
        dto.setViewCount(resource.getViewCount());
        dto.setCreateDate(resource.getCreateDate());
        dto.setUpdateDate(resource.getUpdateDate());
        return dto;
    }
}
