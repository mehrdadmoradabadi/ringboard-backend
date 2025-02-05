package com.RingBoard.wallboard.resource;

import com.RingBoard.wallboard.pbx.PBX;
import com.RingBoard.wallboard.pbx.PBXService;
import com.RingBoard.wallboard.resource.dto.ResourceDto;
import com.RingBoard.wallboard.utils.ResourceNotFoundException;
import com.RingBoard.wallboard.utils.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private PBXService pbxService;

    private ResourceDto.ResourceResponse mapToResponse(Resource resource) {
        ResourceDto.ResourceResponse resourceDto = new ResourceDto.ResourceResponse();
        resourceDto.setId(resource.getId());
        resourceDto.setName(resource.getName());
        resourceDto.setType(resource.getType());
        resourceDto.setUpdatedAt(resource.getUpdatedAt());
        return resourceDto;
    }

    private Resource mapToEntity(ResourceDto.CreateResourceRequest request) {
        PBX pbx = pbxService.findById(request.getPbxId());
        assert pbx != null;
        Resource resource = new Resource();
        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setPbx(pbx);
        return resource;
    }


    @Transactional
    public ResourceDto.ResourceResponse save(ResourceDto.CreateResourceRequest resourceRequest) {
        Resource resource = mapToEntity(resourceRequest);
        resource.setCreatedAt(ZonedDateTime.now());
        resource.setUpdatedAt(ZonedDateTime.now());
        return mapToResponse(resourceRepository.save(resource));
    }

    public SearchResponse<List<ResourceDto.ResourceResponse>> findAll(int page, String search , String sortBy, String sortDirection) {
        int pageSize = 10;
        List<Resource> resources;
        List<ResourceDto.ResourceResponse> resourceDtosList;
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
            resourceDtosList = pagedResources.stream().map(this::mapToResponse).collect(Collectors.toList());
        } else {
            resourceDtosList = resources.stream().map(this::mapToResponse).collect(Collectors.toList());
        }
        return new SearchResponse<>(page, totalPages, resourceDtosList);
    }

    public ResourceDto.ResourceResponse findById(Long id) {
        return mapToResponse(Objects.requireNonNull(resourceRepository.findById(id).orElse(null)));
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }

    public ResourceDto.ResourceResponse update(ResourceDto.UpdateResourceRequest resource) {
        Resource existingResource = resourceRepository.findById(resource.getId()).orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + resource.getId()));
        assert existingResource != null;
        if (resource.getName() != null) existingResource.setName(resource.getName());
        if (resource.getType() != null) existingResource.setType(resource.getType());
        existingResource.setUpdatedAt(ZonedDateTime.now());

        return mapToResponse(resourceRepository.save(existingResource));
    }

    public ResourceDto.ResourceResponse findByName(String name) {
        Resource resource = resourceRepository.findByName(name);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found with name: " + name);
        }
        return mapToResponse(resource);
    }

    public List<ResourceDto.ResourceResponse> findByType(String type) {
        List<Resource> resources = resourceRepository.findByType(type);
        if (resources.isEmpty()) {
            throw new ResourceNotFoundException("No resources found with type: " + type);
        }
        return resources.stream().map(this::mapToResponse).toList();
    }

    public void deleteByName(String name) {
        Resource resource = resourceRepository.findByName(name);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found with name: " + name);
        }
        resourceRepository.deleteByName(name);
    }
    @Transactional
    public void deleteByPBXId(String pbxId) {
        PBX pbx = pbxService.findById(pbxId);
        if (pbx == null) {
            throw new ResourceNotFoundException("PBX not found with ID: " + pbxId);
        }
        resourceRepository.deleteByPbx(pbx);
    }

    public List<ResourceDto.ResourceResponse> findByPBXId(String pbxId) {
        PBX pbx = pbxService.findById(pbxId);
        if (pbx == null) {
            throw new ResourceNotFoundException("PBX not found with ID: " + pbxId);
        }
        List<Resource> resources = resourceRepository.findByPbx(pbx);
        return resources.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

}
