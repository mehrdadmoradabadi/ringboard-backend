package com.wallboard.wallboard.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findByName(String name);

    void deleteByName(String name);
}
