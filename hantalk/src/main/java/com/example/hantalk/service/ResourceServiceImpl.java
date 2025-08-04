package com.example.hantalk.service;

import com.example.hantalk.dto.ResourceDTO;
import com.example.hantalk.entity.Resource;
import com.example.hantalk.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    @Override
    public List<ResourceDTO> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceDTO getResourceById(int resourceId) {
        Resource resource = resourceRepository.findById((int) resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return convertToDTO(resource);
    }

    @Override
    public ResourceDTO createResource(ResourceDTO dto) {
        dto.setCreateDate(LocalDateTime.now());
        Resource resource = convertToEntity(dto);
        Resource saved = resourceRepository.save(resource);
        return convertToDTO(saved);
    }

    @Override
    public ResourceDTO updateResource(int resourceId, ResourceDTO dto) {
        Resource resource = resourceRepository.findById((int) resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.setTitle(dto.getTitle());
        resource.setContent(dto.getContent());
        resource.setArchive(dto.getArchive());
        // create_date는 수정하지 않음
        Resource updated = resourceRepository.save(resource);
        return convertToDTO(updated);
    }

    @Override
    public void deleteResource(int resourceId) {
        resourceRepository.deleteById((int) resourceId);
    }

    // --- 변환 메서드 ---

    private ResourceDTO convertToDTO(Resource entity) {
        ResourceDTO dto = new ResourceDTO();
        dto.setResourceId(entity.getResourceId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setArchive(entity.getArchive());
        dto.setViewCount(entity.getViewCount());
        dto.setCreateDate(entity.getCreateDate());
        return dto;
    }

    private Resource convertToEntity(ResourceDTO dto) {
        Resource entity = new Resource();
        entity.setResourceId(dto.getResourceId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setArchive(dto.getArchive());
        entity.setViewCount(dto.getViewCount());
        entity.setCreateDate(dto.getCreateDate());
        return entity;
    }
}
