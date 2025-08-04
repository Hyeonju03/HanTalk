package com.example.hantalk.service;

import com.example.hantalk.dto.ResourceDTO;

import java.util.List;

public interface ResourceService {
    List<ResourceDTO> getAllResources();
    ResourceDTO getResourceById(int resourceId);
    ResourceDTO createResource(ResourceDTO resourceDTO);
    ResourceDTO updateResource(int resourceId, ResourceDTO resourceDTO);
    void deleteResource(int resourceId);
}
