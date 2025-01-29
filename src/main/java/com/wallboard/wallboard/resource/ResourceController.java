package com.wallboard.wallboard.resource;

import ch.loway.oss.ari4java.tools.ARIException;
import com.wallboard.wallboard.dto.ResourceDto;
import com.wallboard.wallboard.resource.adapters.*;
import com.wallboard.wallboard.utils.ApiResponse;
import com.wallboard.wallboard.utils.IDNormalizer;
import com.wallboard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    private  ResourceService resourceService;




    @Operation(summary = "Create a resource", description = "Create a resource")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ResourceDto>> create(Resource resource) {

        return ResponseEntity.ok(ApiResponse.success(resourceService.create(resource)));
    }

    @Operation(summary = "Update a resource", description = "Update a resource")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<ResourceDto>> update(Resource resource) {

        return ResponseEntity.ok(ApiResponse.success(resourceService.update(resource)));
    }

    @Operation(summary = "Delete a resource", description = "Delete a resource")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(String id) {
        id = IDNormalizer.normalize(id);
            resourceService.delete(Long.valueOf(id));
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully"));
    }

    @Operation(summary = "Find a resource by name", description = "Find a resource by name")
    @GetMapping("/findByName")
    public ResponseEntity<ApiResponse<ResourceDto>> findByName(String name) {
            return ResponseEntity.ok(ApiResponse.success(resourceService.findByName(name)));
    }

    @Operation(summary = "Find a resource by type", description = "Find a resource by type")
    @GetMapping("/findByType")
    public ResponseEntity<ApiResponse<List<ResourceDto>>> findByType(String type) {

        return ResponseEntity.ok(ApiResponse.success(resourceService.findByType(type)));
    }

    @Operation(summary = "Find all resources", description = "Find all resources")
    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse<SearchResponse<List<ResourceDto>>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc" ) String order,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(ApiResponse.success(resourceService.findAll(page, search, sortBy, order)));
    }

    @Operation(summary = "Find a resource by id", description = "Find a resource by id")
    @GetMapping("/findById")
    public ResponseEntity<ApiResponse<ResourceDto>> findById(String id) {

            id = IDNormalizer.normalize(id);
            return ResponseEntity.ok(ApiResponse.success(resourceService.findById(Long.valueOf(id))));
    }

    @Operation(summary = "Delete a resource by name", description = "Delete a resource by name")
    @DeleteMapping("/deleteByName")
    public ResponseEntity<ApiResponse<String>> deleteByName(String name) {

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

    @GetMapping("/agents")
    public ResponseEntity<ApiResponse<List<AgentInfo>>> getAgents(@RequestParam String pbxID) throws ARIException {

            pbxID = IDNormalizer.normalize(pbxID);
            return ResponseEntity.ok(ApiResponse.success(asteriskService.getAgents(pbxID)));
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

    @Operation(summary = "Stream updates from a PBX", description = "Stream updates from a PBX")
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamPBXUpdates(@RequestBody HashMap<String, String> data) {
        return asteriskService.streamUpdates(data.get("pbxId"), data.get("interval"));
    }

}
