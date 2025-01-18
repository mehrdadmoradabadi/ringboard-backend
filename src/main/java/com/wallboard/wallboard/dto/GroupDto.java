package com.wallboard.wallboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GroupDto {
    private Long id;
    private String name;
    private List<String> users;

}
