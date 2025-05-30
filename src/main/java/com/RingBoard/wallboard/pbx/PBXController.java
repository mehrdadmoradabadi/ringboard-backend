package com.RingBoard.wallboard.pbx;

import com.RingBoard.wallboard.pbx.dto.PBXDtos;
import com.RingBoard.wallboard.utils.ApiResponse;
import com.RingBoard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pbx")
@Tag(name = "PBX Management", description = "Endpoints for managing PBX systems")
public class PBXController {
    @Autowired
    private PBXService pbxService;
    @Autowired
    private PBXSyncService pbxSyncService;



    @Operation(summary = "Find all pbx", description = "Find all pbx and return the list of pbx")
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<SearchResponse<List<PBXDtos.PBXResponse>>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(pbxService.findAll(page, search, sortBy, order)));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Save a pbx", description = "Save a pbx and return the saved pbx")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<PBXDtos.PBXResponse>> save(
            @Valid @RequestBody PBXDtos.CreatePBXRequest pbxRequest) {
        return ResponseEntity.ok(ApiResponse.success(pbxService.save(pbxRequest)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a pbx", description = "Delete a pbx")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(
            @RequestBody PBXDtos.UpdatePBXRequest pbxRequest) {
        pbxService.delete(pbxRequest);
        return ResponseEntity.ok(ApiResponse.success("PBX deleted successfully"));
    }

    @Operation(summary = "Find a pbx by name", description = "Find a pbx by name and return the pbx")
    @GetMapping("/findByName/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<PBXDtos.PBXResponse>> findByName(
            @PathVariable String name) {
        return ResponseEntity.ok(ApiResponse.success(pbxService.findByName(name)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a pbx", description = "Update a pbx and return the updated pbx")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<PBXDtos.PBXResponse>> update(
            @Valid @RequestBody PBXDtos.UpdatePBXRequest pbxRequest) {
        return ResponseEntity.ok(ApiResponse.success(pbxService.update(pbxRequest)));
    }

    @Operation(summary = "Sync PBX resources", description = "Fetch all resources from PBX and save them to database")
    @PostMapping("/{pbxId}/sync-resources")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<String>> syncResources(@PathVariable String pbxId) {
        pbxSyncService.syncPBXResources(pbxId);
        return ResponseEntity.ok(ApiResponse.success("PBX resources synchronized successfully"));
    }

    @Operation(summary = "Refresh PBX resources", description = "Delete existing resources and fetch fresh data from PBX")
    @PostMapping("/{pbxId}/refresh-resources")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<String>> refreshResources(@PathVariable String pbxId) {
        pbxSyncService.refreshPBXResources(pbxId);
        return ResponseEntity.ok(ApiResponse.success("PBX resources refreshed successfully"));
    }

//This is for test. TODO remove later
    @GetMapping("/threads")
    public List<String> listActiveThreads() {

        List<String> threadNames = new ArrayList<>();
        Thread.getAllStackTraces().keySet().forEach(thread -> threadNames.add(thread.getName()));
        return threadNames;
    }


}
