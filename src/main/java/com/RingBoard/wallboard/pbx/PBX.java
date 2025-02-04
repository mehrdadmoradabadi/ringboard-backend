package com.RingBoard.wallboard.pbx;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.RingBoard.wallboard.resource.Resource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity(name = "PBX")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PBX {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "host", nullable = false)
    private String host;
    @Column(name = "ari_port")
    private String ariPort;
    @Column(name = "ami_port")
    private String amiPort;
    @Column(name = "protocol")
    private String protocol;
    @Column(name = "username", nullable = false)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @OneToMany(mappedBy = "pbx", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Resource> resources;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "app_name", nullable = false)
    private String appName;
}
