package com.wallboard.wallboard.resource.adapters;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionInfo {
    private String number;
    private String status;
}

