package com.wallboard.wallboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
public class ResourceDto {
    private Long id;
    private String name;
    private String type;
    private Map<String, Object > metadata;
    private ZonedDateTime updatedAt;
}
