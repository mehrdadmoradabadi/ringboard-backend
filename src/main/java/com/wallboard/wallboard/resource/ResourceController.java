package com.wallboard.wallboard.resource;

import com.wallboard.wallboard.dto.ResourceDto;
import com.wallboard.wallboard.utils.ApiResponse;
import com.wallboard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @Operation(summary = "Create a resource", description = "Create a resource")
    @PostMapping("/create")
    public ApiResponse<ResourceDto> create(Resource resource) {
        return new ApiResponse<>(resourceService.create(resource));
    }

    @Operation(summary = "Update a resource", description = "Update a resource")
    @PatchMapping("/update")
    public ApiResponse<ResourceDto> update(Resource resource) {
        return new ApiResponse<>(resourceService.update(resource));
    }

    @Operation(summary = "Delete a resource", description = "Delete a resource")
    @DeleteMapping("/delete")
    public ApiResponse<String> delete(Long id) {
        resourceService.delete(id);
        return new ApiResponse<>("Resource deleted successfully");
    }

    @Operation(summary = "Find a resource by name", description = "Find a resource by name")
    @GetMapping("/findByName")
    public ApiResponse<ResourceDto> findByName(String name) {
        return new ApiResponse<>(resourceService.findByName(name)) ;
    }

    @Operation(summary = "Find a resource by type", description = "Find a resource by type")
    @GetMapping("/findByType")
    public ApiResponse<List<ResourceDto>> findByType(String type) {
        return new ApiResponse<>(resourceService.findByType(type));
    }

    @Operation(summary = "Find a resource by metadata", description = "Find a resource by metadata")
    @GetMapping("/findByMetadata")
    public ApiResponse<List<ResourceDto>> findByMetadata(String key, Object value) {
        return new ApiResponse<>(resourceService.findByMetadata(key, value));
    }

    @Operation(summary = "Find all resources", description = "Find all resources")
    @GetMapping("/findAll")
    public ApiResponse<SearchResponse<List<ResourceDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc" ) String order,
            @RequestParam(required = false) String search) {
        return new ApiResponse<>(resourceService.findAll(page, search, sortBy, order));
    }

    @Operation(summary = "Find a resource by id", description = "Find a resource by id")
    @GetMapping("/findById")
    public ApiResponse<ResourceDto> findById(Long id) {
        return new ApiResponse<>(resourceService.findById(id));
    }

    @Operation(summary = "Delete a resource by name", description = "Delete a resource by name")
    @DeleteMapping("/deleteByName")
    public ApiResponse<String> deleteByName(String name) {
        resourceService.deleteByName(name);
        return new ApiResponse<>("Resource deleted successfully");
    }

    @Operation(summary = "Delete a resource by pbx", description = "Delete a resource by pbx")
    @DeleteMapping("/deleteByPbx")
    public ApiResponse<String> deleteByPbx(Long pbxId) {
        resourceService.deleteByPbxId(pbxId);
        return new ApiResponse<>("Resource deleted successfully");
    }
}
