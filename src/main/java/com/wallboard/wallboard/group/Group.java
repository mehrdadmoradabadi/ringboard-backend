package com.wallboard.wallboard.group;

import com.wallboard.wallboard.resource.Resource;
import com.wallboard.wallboard.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @ManyToMany(mappedBy = "groups")
    private Set<User> users;

    @ManyToMany
    @JoinTable(name = "group_resource",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private Set<Resource> resources;

    public Group(String name) {
        this.name = name;
    }
}
