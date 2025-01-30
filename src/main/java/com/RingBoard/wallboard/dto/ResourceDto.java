package com.RingBoard.wallboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class ResourceDto {
    private Long id;
    private String name;
    private String type;
    private ZonedDateTime updatedAt;
}
