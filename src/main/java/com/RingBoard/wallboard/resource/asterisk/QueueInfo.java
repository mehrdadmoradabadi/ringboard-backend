package com.RingBoard.wallboard.resource.asterisk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueInfo {
    private String name;
    private Integer memberCount;
    private Integer abandoned;
    private Integer waiting;
    private Integer completed;


}

