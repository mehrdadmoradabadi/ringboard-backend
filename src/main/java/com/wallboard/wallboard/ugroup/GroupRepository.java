package com.wallboard.wallboard.ugroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<UGroup, Long> {

    UGroup findByName(String name);

    void deleteByName(String name);

    List<UGroup> findByNameContainingIgnoreCase(String search);
}
