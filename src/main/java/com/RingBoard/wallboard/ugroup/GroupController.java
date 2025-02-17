package com.RingBoard.wallboard.ugroup;

import com.RingBoard.wallboard.ugroup.dto.GroupDto;
import com.RingBoard.wallboard.utils.ApiResponse;
import com.RingBoard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;


    @Operation(summary = "Save a Group", description = "Save a Group and return the saved Group")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<GroupDto.GroupResponse>> save(@Valid @RequestBody GroupDto.CreateGroupResponse Group) {

            return ResponseEntity.ok(ApiResponse.success(groupService.save(Group)));
    }

    @Operation(summary = "Delete a Group", description = "Delete a Group")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@Valid@RequestBody Group Group) {
            groupService.delete(Group);
            return ResponseEntity.ok(ApiResponse.success("Group deleted successfully"));
    }

    @Operation(summary = "Delete a group by name", description = "Delete a group by name")
    @DeleteMapping("/deleteByName")
    public ResponseEntity<ApiResponse<String>> deleteByName(@Valid @RequestBody String name) {
            groupService.deleteByName(name);
            return ResponseEntity.ok(ApiResponse.success("Group deleted successfully"));
    }

    @Operation(summary = "Find a group by name", description = "Find a group by name and return the group")
    @GetMapping("/findByName")
    public ResponseEntity<ApiResponse<GroupDto.GroupResponse>> findByName(@PathVariable String name) {
            return ResponseEntity.ok(ApiResponse.success(groupService.findByName(name)));
    }

    @Operation(summary = "Find all groups", description = "Find all groups and return the list of groups")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<SearchResponse<List<GroupDto.GroupResponse>>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc" ) String order,
            @RequestParam(required = false) String search) {
            return ResponseEntity.ok(ApiResponse.success(groupService.findAll(page, search, sortBy, order)));
    }

    @Operation(summary = "Update a Group", description = "Update a Group and return the updated Group")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<GroupDto.GroupResponse>> update(@Valid@RequestBody GroupDto.UpdateGroupResponse updatedGroup) {
        return ResponseEntity.ok(ApiResponse.success(groupService.update(updatedGroup)));
    }

    @Operation(summary = "Find a group by id", description = "Find a group by id and return the group")
    @GetMapping("/findById")
    public ResponseEntity<ApiResponse<GroupDto.GroupResponse>> findById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(groupService.findById(id)));
    }
}
