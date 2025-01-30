package com.RingBoard.wallboard.ugroup;

import com.RingBoard.wallboard.dto.GroupDto;
import com.RingBoard.wallboard.utils.ApiResponse;
import com.RingBoard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;


    @Operation(summary = "Save a UGroup", description = "Save a UGroup and return the saved UGroup")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<GroupDto>> save(@RequestBody UGroup UGroup) {

            return ResponseEntity.ok(ApiResponse.success(groupService.save(UGroup)));
    }

    @Operation(summary = "Delete a UGroup", description = "Delete a UGroup")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestBody UGroup UGroup) {
            groupService.delete(UGroup);
            return ResponseEntity.ok(ApiResponse.success("UGroup deleted successfully"));
    }

    @Operation(summary = "Delete a group by name", description = "Delete a group by name")
    @DeleteMapping("/deleteByName")
    public ResponseEntity<ApiResponse<String>> deleteByName( @RequestBody String name) {
            groupService.deleteByName(name);
            return ResponseEntity.ok(ApiResponse.success("UGroup deleted successfully"));
    }

    @Operation(summary = "Find a group by name", description = "Find a group by name and return the group")
    @GetMapping("/findByName")
    public ResponseEntity<ApiResponse<GroupDto>> findByName(@PathVariable String name) {
            return ResponseEntity.ok(ApiResponse.success(groupService.findByName(name)));
    }

    @Operation(summary = "Find all groups", description = "Find all groups and return the list of groups")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<SearchResponse<List<GroupDto>>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc" ) String order,
            @RequestParam(required = false) String search) {
            return ResponseEntity.ok(ApiResponse.success(groupService.findAll(page, search, sortBy, order)));
    }

    @Operation(summary = "Update a UGroup", description = "Update a UGroup and return the updated UGroup")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<GroupDto>> update(@RequestBody UGroup UGroup) {
        return ResponseEntity.ok(ApiResponse.success(groupService.update(UGroup)));
    }

    @Operation(summary = "Find a group by id", description = "Find a group by id and return the group")
    @GetMapping("/findById")
    public ResponseEntity<ApiResponse<GroupDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(groupService.findById(id)));
    }
}
