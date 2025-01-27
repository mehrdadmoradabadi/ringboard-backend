package com.wallboard.wallboard.resource.adapters;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrunkInfo {
    private String name;
    private String status;

}