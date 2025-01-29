package com.wallboard.wallboard.resource;

import ch.loway.oss.ari4java.tools.RestException;
import com.wallboard.wallboard.dto.ResourceDto;
import com.wallboard.wallboard.resource.adapters.*;
import com.wallboard.wallboard.utils.ApiResponse;
import com.wallboard.wallboard.utils.SearchResponse;
import com.wallboard.wallboard.utils.IDNormalizer;
import io.swagger.v3.oas.annotations.Operation;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        try {
        return ResponseEntity.ok(ApiResponse.success(resourceService.create(resource)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Update a resource", description = "Update a resource")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<ResourceDto>> update(Resource resource) {
        try {
        return ResponseEntity.ok(ApiResponse.success(resourceService.update(resource)));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }

    }

    @Operation(summary = "Delete a resource", description = "Delete a resource")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(String id) {
        try {
        id = IDNormalizer.normalize(id);
            resourceService.delete(Long.valueOf(id));
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully"));
        }catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Find a resource by name", description = "Find a resource by name")
    @GetMapping("/findByName")
    public ResponseEntity<ApiResponse<ResourceDto>> findByName(String name) {
        try {
            return ResponseEntity.ok(ApiResponse.success(resourceService.findByName(name)));
        }catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Find a resource by type", description = "Find a resource by type")
    @GetMapping("/findByType")
    public ResponseEntity<ApiResponse<List<ResourceDto>>> findByType(String type) {
        try {
        return ResponseEntity.ok(ApiResponse.success(resourceService.findByType(type)));
        }catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Find all resources", description = "Find all resources")
    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse<SearchResponse<List<ResourceDto>>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc" ) String order,
            @RequestParam(required = false) String search) {
        try {
        return ResponseEntity.ok(ApiResponse.success(resourceService.findAll(page, search, sortBy, order)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Find a resource by id", description = "Find a resource by id")
    @GetMapping("/findById")
    public ResponseEntity<ApiResponse<ResourceDto>> findById(String id) {
        try {
            id = IDNormalizer.normalize(id);
            return ResponseEntity.ok(ApiResponse.success(resourceService.findById(Long.valueOf(id))));
        }catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Delete a resource by name", description = "Delete a resource by name")
    @DeleteMapping("/deleteByName")
    public ResponseEntity<ApiResponse<String>> deleteByName(String name) {
        try {
        resourceService.deleteByName(name);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully"));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.internalError(e.getMessage()));
        }
    }

    @Autowired
    private AriService asteriskService;
    @GetMapping("/queues")
    public ResponseEntity<ApiResponse<List<QueueInfo>>> getQueues(@RequestParam String pbxID) {
        try {
        pbxID = IDNormalizer.normalize(pbxID);
            return ResponseEntity.ok(ApiResponse.success(asteriskService.getQueues(pbxID)));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @GetMapping("/agents")
    public ResponseEntity<ApiResponse<List<AgentInfo>>> getAgents(@RequestParam String pbxID) {
        try {
            pbxID = IDNormalizer.normalize(pbxID);
            return ResponseEntity.ok(ApiResponse.success(asteriskService.getAgents(pbxID)));
        } catch (RestException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.internalError(e.getMessage()));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @GetMapping("/extensions")
    public ResponseEntity<ApiResponse<List<ExtensionInfo>>> getExtensions(@RequestParam String pbxID) {
        try {
            pbxID = IDNormalizer.normalize(pbxID);
            return ResponseEntity.ok(ApiResponse.success(asteriskService.getExtensions(pbxID)));
        } catch (RestException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError(e.getMessage()));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @GetMapping("/trunks")
    public ResponseEntity<ApiResponse<List<TrunkInfo>>> getTrunks(@RequestParam String pbxID) {
        try {
            pbxID = IDNormalizer.normalize(pbxID);
            return ResponseEntity.ok(ApiResponse.success(asteriskService.getTrunks(pbxID)));
        } catch (RestException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError(e.getMessage()));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Stream updates from a PBX", description = "Stream updates from a PBX")
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamPBXUpdates(@RequestBody HashMap<String, String> data) {
        return asteriskService.streamUpdates(data.get("pbxId"), data.get("interval"));
    }

}
