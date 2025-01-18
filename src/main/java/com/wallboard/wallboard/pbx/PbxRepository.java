package com.wallboard.wallboard.pbx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PbxRepository extends JpaRepository<PBX, Integer> {
    PBX findByName(String name);

    void deleteByName(String name);

}
