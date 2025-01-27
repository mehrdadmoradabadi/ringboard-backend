package com.wallboard.wallboard.pbx;

import com.wallboard.wallboard.dto.PBXDto;
import com.wallboard.wallboard.utils.ApiResponse;
import com.wallboard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pbx")
public class PBXController {
    @Autowired
    private PBXService pbxService;


    @Operation(summary = "Find all pbx", description = "Find all pbx and return the list of pbx")
    @GetMapping("/all")
    public ApiResponse<SearchResponse<List<PBXDto>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(required = false) String sortBy,
                                                             @RequestParam(defaultValue = "asc" ) String order,
                                                             @RequestParam(required = false) String search) {
        return new ApiResponse<>(pbxService.findAll(page,search , sortBy, order));
    }

    @Operation(summary = "Save a pbx", description = "Save a pbx and return the saved pbx")
    @PostMapping("/save")
    public ApiResponse<PBXDto> save(@RequestBody PBX pbx) {
        return new ApiResponse<>(pbxService.save(pbx));
    }

    @Operation(summary = "Delete a pbx", description = "Delete a pbx")
    @DeleteMapping("/delete")
    public ApiResponse<String> delete(@RequestBody PBX pbx) {
        pbxService.delete(pbx);
        return new ApiResponse<>("PBX deleted successfully");
    }

    @Operation(summary = "Delete a pbx by name", description = "Delete a pbx by name")
    @DeleteMapping("/deleteByName")
    public ApiResponse<String> deleteByName( @RequestBody String name) {
        pbxService.deleteByName(name);
        return new ApiResponse<>("PBX deleted successfully");
    }

    @Operation(summary = "Find a pbx by name", description = "Find a pbx by name and return the pbx")
    @GetMapping("/findByName")
    public ApiResponse<PBXDto> findByName(@PathVariable String name) {
        return new ApiResponse<>(pbxService.findByName(name));
    }

    @Operation(summary = "Update a pbx", description = "Update a pbx and return the updated pbx")
    @PatchMapping("/update")
    public ApiResponse<PBXDto> update(PBX pbx) {

        return new ApiResponse<>( pbxService.update(pbx));
    }


//This is for test. TODO remove later
    @GetMapping("/threads")
    public List<String> listActiveThreads() {

        List<String> threadNames = new ArrayList<>();
        Thread.getAllStackTraces().keySet().forEach(thread -> threadNames.add(thread.getName()));
        return threadNames;
    }


}
