package com.wallboard.wallboard.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse<T> {
    private int page;
    private long total;
    private T ObjectList;
}
