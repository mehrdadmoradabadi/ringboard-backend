package com.wallboard.wallboard.ugroup;

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


    @Operation(summary = "Save a UGroup", description = "Save a UGroup and return the saved UGroup")
    @PostMapping("/save")
    public ApiResponse<GroupDto> save(@RequestBody UGroup UGroup) {
        return new ApiResponse<>(groupService.save(UGroup));
    }

    @Operation(summary = "Delete a UGroup", description = "Delete a UGroup")
    @DeleteMapping("/delete")
    public ApiResponse<String> delete(@RequestBody UGroup UGroup) {
        groupService.delete(UGroup);
        return new ApiResponse<>("UGroup deleted successfully");
    }

    @Operation(summary = "Delete a group by name", description = "Delete a group by name")
    @DeleteMapping("/deleteByName")
    public ApiResponse<String> deleteByName( @RequestBody String name) {
        groupService.deleteByName(name);
        return new ApiResponse<>("UGroup deleted successfully");
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

    @Operation(summary = "Update a UGroup", description = "Update a UGroup and return the updated UGroup")
    @PatchMapping("/update")
    public ApiResponse<GroupDto> update(@RequestBody UGroup UGroup) {
        return new ApiResponse<>(groupService.update(UGroup));
    }

    @Operation(summary = "Find a group by id", description = "Find a group by id and return the group")
    @GetMapping("/findById")
    public GroupDto findById(@PathVariable Long id) {
        return groupService.findById(id);
    }
}
