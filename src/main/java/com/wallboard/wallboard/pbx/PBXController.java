package com.wallboard.wallboard.pbx;

import com.wallboard.wallboard.dto.PBXDto;
import com.wallboard.wallboard.pbx.adapters.Issabel;
import com.wallboard.wallboard.pbx.adapters.IssabelService;
import com.wallboard.wallboard.pbx.adapters.PbxUpdaterService;
import com.wallboard.wallboard.utils.ApiResponse;
import com.wallboard.wallboard.utils.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/pbx")
public class PBXController {
    @Autowired
    private PBXService pbxService;
    @Autowired
    private IssabelService issabelService;


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

    @Autowired
    private PbxUpdaterService pbxUpdaterService;
    @Operation(summary = "Stream updates from a PBX", description = "Stream updates from a PBX")
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Issabel streamPBXUpdates(@RequestBody HashMap<String, String> data) {
        return pbxUpdaterService.getIssabelInstance(data.get("pbxId"), data.get("host"), data.get("username"), data.get("password"));
    }
//    @Operation(summary = "Stream updates from a PBX", description = "Stream updates from a PBX")
//    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> streamPBXUpdates(@RequestBody HashMap<String, String> data) {
//        return issabelService.streamUpdates(data.get("pbxId"), data.get("host"), data.get("username"), data.get("password"), data.get("interval"));
//    }

    @GetMapping("/threads")
    public List<String> listActiveThreads() {

        List<String> threadNames = new ArrayList<>();
        Thread.getAllStackTraces().keySet().forEach(thread -> threadNames.add(thread.getName()));
        return threadNames;
    }
}
