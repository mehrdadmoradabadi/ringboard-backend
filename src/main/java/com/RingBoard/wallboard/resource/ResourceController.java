package com.RingBoard.wallboard.resource;

import ch.loway.oss.ari4java.tools.ARIException;
import com.RingBoard.wallboard.resource.asterisk.AriService;
import com.RingBoard.wallboard.resource.asterisk.ExtensionInfo;
import com.RingBoard.wallboard.resource.asterisk.QueueInfo;
import com.RingBoard.wallboard.resource.asterisk.TrunkInfo;
import com.RingBoard.wallboard.resource.dto.ResourceDto;
import com.RingBoard.wallboard.utils.ApiResponse;
import com.RingBoard.wallboard.utils.IDNormalizer;
import com.RingBoard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    private  ResourceService resourceService;




    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a resource", description = "Create a resource")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ResourceDto.ResourceResponse>> create(@Valid  @RequestBody ResourceDto.CreateResourceRequest resource) {

        return ResponseEntity.ok(ApiResponse.success(resourceService.save(resource)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a resource", description = "Update a resource")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<ResourceDto.ResourceResponse>> update(@Valid @RequestBody ResourceDto.UpdateResourceRequest resource) {

        return ResponseEntity.ok(ApiResponse.success(resourceService.update(resource)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a resource", description = "Delete a resource")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestBody String id) {
        id = IDNormalizer.normalize(id);
            resourceService.delete(Long.valueOf(id));
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully"));
    }

    @Operation(summary = "Find a resource by name", description = "Find a resource by name")
    @GetMapping("/findByName")
    public ResponseEntity<ApiResponse<ResourceDto.ResourceResponse>> findByName(@RequestBody String name) {
            return ResponseEntity.ok(ApiResponse.success(resourceService.findByName(name)));
    }

    @Operation(summary = "Find a resource by type", description = "Find a resource by type")
    @GetMapping("/findByType")
    public ResponseEntity<ApiResponse<List<ResourceDto.ResourceResponse>>> findByType(@RequestBody String type) {

        return ResponseEntity.ok(ApiResponse.success(resourceService.findByType(type)));
    }

    @Operation(summary = "Find all resources", description = "Find all resources")
    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse<SearchResponse<List<ResourceDto.ResourceResponse>>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc" ) String order,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(ApiResponse.success(resourceService.findAll(page, search, sortBy, order)));
    }

    @Operation(summary = "Find a resource by id", description = "Find a resource by id")
    @GetMapping("/findById")
    public ResponseEntity<ApiResponse<ResourceDto.ResourceResponse>> findById(@RequestBody String id) {

            id = IDNormalizer.normalize(id);
            return ResponseEntity.ok(ApiResponse.success(resourceService.findById(Long.valueOf(id))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a resource by name", description = "Delete a resource by name")
    @DeleteMapping("/deleteByName")
    public ResponseEntity<ApiResponse<String>> deleteByName(@RequestBody String name) {

        resourceService.deleteByName(name);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully"));
    }

    @Autowired
    private AriService asteriskService;
    @GetMapping("/queues")
    public ResponseEntity<ApiResponse<List<QueueInfo>>> getQueues(@RequestParam String pbxID) {
        pbxID = IDNormalizer.normalize(pbxID);
            return ResponseEntity.ok(ApiResponse.success(asteriskService.getQueues(pbxID)));
    }



    @GetMapping("/extensions")
    public ResponseEntity<ApiResponse<List<ExtensionInfo>>> getExtensions(@RequestParam String pbxID) throws ARIException {
            pbxID = IDNormalizer.normalize(pbxID);
            return ResponseEntity.ok(ApiResponse.success(asteriskService.getExtensions(pbxID)));
    }

    @GetMapping("/trunks")
    public ResponseEntity<ApiResponse<List<TrunkInfo>>> getTrunks(@RequestParam String pbxID) throws ARIException {
            pbxID = IDNormalizer.normalize(pbxID);
            return ResponseEntity.ok(ApiResponse.success(asteriskService.getTrunks(pbxID)));
    }

}
