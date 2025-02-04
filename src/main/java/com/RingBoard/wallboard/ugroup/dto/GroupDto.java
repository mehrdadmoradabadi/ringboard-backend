package com.RingBoard.wallboard.ugroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;

public class GroupDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateGroupResponse{
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;
        private String description;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private Set<String> users;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateGroupResponse{
        private Long id;
        private String name;
        private String description;
        private ZonedDateTime updatedAt;
        private Set<String> users;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupResponse{
        private Long id;
        private String name;
        private String description;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private Set<String> users;
    }


}
