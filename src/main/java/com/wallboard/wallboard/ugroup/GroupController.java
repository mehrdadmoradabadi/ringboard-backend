package com.wallboard.wallboard.ugroup;

import com.wallboard.wallboard.dto.GroupDto;
import com.wallboard.wallboard.utils.ApiResponse;
import com.wallboard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        try {
            return ResponseEntity.ok(ApiResponse.success(groupService.save(UGroup)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Delete a UGroup", description = "Delete a UGroup")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestBody UGroup UGroup) {
        try {
            groupService.delete(UGroup);
            return ResponseEntity.ok(ApiResponse.success("UGroup deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
    }
    }

    @Operation(summary = "Delete a group by name", description = "Delete a group by name")
    @DeleteMapping("/deleteByName")
    public ResponseEntity<ApiResponse<String>> deleteByName( @RequestBody String name) {
        try {
            groupService.deleteByName(name);
            return ResponseEntity.ok(ApiResponse.success("UGroup deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Find a group by name", description = "Find a group by name and return the group")
    @GetMapping("/findByName")
    public ResponseEntity<ApiResponse<GroupDto>> findByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(ApiResponse.success(groupService.findByName(name)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Find all groups", description = "Find all groups and return the list of groups")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<SearchResponse<List<GroupDto>>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc" ) String order,
            @RequestParam(required = false) String search) {

        try {
            return ResponseEntity.ok(ApiResponse.success(groupService.findAll(page, search, sortBy, order)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }

    }

    @Operation(summary = "Update a UGroup", description = "Update a UGroup and return the updated UGroup")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<GroupDto>> update(@RequestBody UGroup UGroup) {
        try {
            return ResponseEntity.ok(ApiResponse.success(groupService.update(UGroup)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @Operation(summary = "Find a group by id", description = "Find a group by id and return the group")
    @GetMapping("/findById")
    public ResponseEntity<ApiResponse<GroupDto>> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(groupService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
