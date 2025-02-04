package com.RingBoard.wallboard.pbx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

public class PBXDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePBXRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;

        @NotBlank(message = "Host is required")
        private String host;

        private String ariPort;
        private String amiPort;
        private String protocol;

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        @NotBlank(message = "App name is required")
        private String appName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePBXRequest {
        private Integer id;
        private String name;
        private String host;
        private String ariPort;
        private String amiPort;
        private String protocol;
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PBXResponse {
        private Integer id;
        private String name;
        private String host;
        private String ariPort;
        private String amiPort;
        private String protocol;
        private String username;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private boolean isActive;
        private String appName;
    }
}