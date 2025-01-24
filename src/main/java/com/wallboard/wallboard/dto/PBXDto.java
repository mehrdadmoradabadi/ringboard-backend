package com.wallboard.wallboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PBXDto {
    private Integer id;
    private String name;
    private String host;
    private String port;
    private String protocol;
    private String username;
}
