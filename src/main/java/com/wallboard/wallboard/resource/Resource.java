package com.wallboard.wallboard.resource;

import com.wallboard.wallboard.pbx.PBX;
import com.wallboard.wallboard.utils.MetadataConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Map;

@Entity
@Data
@Table(name = "resources")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pbx_id", nullable = false)
    private PBX pbx;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = MetadataConverter.class)
    private Map<String, Object> metadata;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    public Resource(String name, String type, Map<String, Object> metadata) {
        this.name = name;
        this.type = type;
        this.metadata = metadata;
    }
}
