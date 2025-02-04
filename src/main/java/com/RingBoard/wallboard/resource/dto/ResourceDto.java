package com.RingBoard.wallboard.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

public class ResourceDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  CreateResourceRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;
        @NotBlank(message = "Type is required")
        private String type;
        @NotBlank(message = "PBX ID is required")
        private String pbxId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  UpdateResourceRequest {
        private Long id;
        private String name;
        private String type;
        private String pbxId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  ResourceResponse {
        private Long id;
        private String name;
        private String type;
        private String pbxId;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
    }
}
