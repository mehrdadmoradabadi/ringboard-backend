package com.wallboard.wallboard.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findByName(String name);

    void deleteByName(String name);

    List<Group> findByNameContainingIgnoreCase(String search);
}
