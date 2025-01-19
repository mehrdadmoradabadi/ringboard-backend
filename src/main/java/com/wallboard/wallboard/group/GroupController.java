package com.wallboard.wallboard.group;

import com.wallboard.wallboard.dto.GroupDto;
import com.wallboard.wallboard.utils.ApiResponse;
import com.wallboard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;


    @Operation(summary = "Save a group", description = "Save a group and return the saved group")
    @PostMapping("/save")
    public ApiResponse<GroupDto> save(@RequestBody Group group) {
        return new ApiResponse<>(groupService.save(group));
    }

    @Operation(summary = "Delete a group", description = "Delete a group")
    @DeleteMapping("/delete")
    public ApiResponse<String> delete(@RequestBody Group group) {
        groupService.delete(group);
        return new ApiResponse<>("Group deleted successfully");
    }

    @Operation(summary = "Delete a group by name", description = "Delete a group by name")
    @DeleteMapping("/deleteByName")
    public ApiResponse<String> deleteByName( @RequestBody String name) {
        groupService.deleteByName(name);
        return new ApiResponse<>("Group deleted successfully");
    }

    @Operation(summary = "Find a group by name", description = "Find a group by name and return the group")
    @GetMapping("/findByName")
    public ApiResponse<GroupDto> findByName(@PathVariable String name) {
        return new ApiResponse<>(groupService.findByName(name));
    }

    @Operation(summary = "Find all groups", description = "Find all groups and return the list of groups")
    @GetMapping("/all")
    public ApiResponse<SearchResponse<List<GroupDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc" ) String order,
            @RequestParam(required = false) String search) {

        return new ApiResponse<>(groupService.findAll(page, search, sortBy, order));

    }

    @Operation(summary = "Update a group", description = "Update a group and return the updated group")
    @PatchMapping("/update")
    public ApiResponse<GroupDto> update(@RequestBody Group group) {
        return new ApiResponse<>(groupService.update(group));
    }

    @Operation(summary = "Find a group by id", description = "Find a group by id and return the group")
    @GetMapping("/findById")
    public GroupDto findById(@PathVariable Long id) {
        return groupService.findById(id);
    }
}
