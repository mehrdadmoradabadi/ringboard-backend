package com.wallboard.wallboard.resource;

import com.wallboard.wallboard.dto.ResourceDto;
import com.wallboard.wallboard.utils.ResourceNotFoundException;
import com.wallboard.wallboard.utils.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    private ResourceDto mapToDto(Resource resource) {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(resource.getId());
        resourceDto.setName(resource.getName());
        resourceDto.setType(resource.getType());
        resourceDto.setUpdatedAt(resource.getUpdatedAt());
        return resourceDto;
    }
    public ResourceDto create(Resource resource) {
        return mapToDto(resourceRepository.save(resource));
    }

    public SearchResponse<List<ResourceDto>> findAll(int page, String search , String sortBy, String sortDirection) {
        int pageSize = 10;
        List<Resource> resources;
        List<ResourceDto> resourceDtosList;
        long totalResources;
        if (search != null && !search.isEmpty()) {
            resources = resourceRepository.findByNameContainingIgnoreCase(search);
            totalResources= resources.size();
        } else {
            resources = resourceRepository.findAll();
            totalResources = resourceRepository.count();
        }
        if(sortBy!=null) {
            Comparator<Resource> comparator = switch (sortBy.toLowerCase()) {
                case "name" -> Comparator.comparing(Resource::getName);
                case "type" -> Comparator.comparing(Resource::getType);
                case "created_at" -> Comparator.comparing(Resource::getCreatedAt);
                case "updated_at" -> Comparator.comparing(Resource::getUpdatedAt);
                default -> throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
            };
            if(sortDirection.equalsIgnoreCase("desc")){
                resources.sort(comparator.reversed());
            }
            resources.sort(comparator);
        }
        long totalPages = (totalResources + pageSize - 1) / pageSize;
        if(page!=0){
            List<Resource> pagedResources = resources.stream()
                    .skip((long) (page - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
            resourceDtosList = pagedResources.stream().map(this::mapToDto).toList();
        } else {
            resourceDtosList = resources.stream().map(this::mapToDto).toList();
        }
        return new SearchResponse<>(page, totalPages, resourceDtosList);
    }

    public ResourceDto findById(Long id) {
        return mapToDto(Objects.requireNonNull(resourceRepository.findById(id).orElse(null)));
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }

    public ResourceDto update(Resource resource) {
        Resource existingResource = resourceRepository.findById(resource.getId()).orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + resource.getId()));
        assert existingResource != null;
        existingResource.setName(resource.getName());
        existingResource.setType(resource.getType());
        existingResource.setUpdatedAt(ZonedDateTime.now());

        return mapToDto(resourceRepository.save(existingResource));
    }

    public ResourceDto findByName(String name) {
        Resource resource = resourceRepository.findByName(name);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found with name: " + name);
        }
        return mapToDto(resource);
    }

    public List<ResourceDto> findByType(String type) {
        List<Resource> resources = resourceRepository.findByType(type);
        if (resources.isEmpty()) {
            throw new ResourceNotFoundException("No resources found with type: " + type);
        }
        return resources.stream().map(this::mapToDto).toList();
    }

    public void deleteByName(String name) {
        Resource resource = resourceRepository.findByName(name);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found with name: " + name);
        }
        resourceRepository.deleteByName(name);
    }

}
