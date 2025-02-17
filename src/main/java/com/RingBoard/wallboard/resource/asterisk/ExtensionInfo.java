package com.RingBoard.wallboard.resource.asterisk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionInfo {
    private String number;
    private String status;
}

