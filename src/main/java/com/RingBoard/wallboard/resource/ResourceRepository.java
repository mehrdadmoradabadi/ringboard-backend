package com.RingBoard.wallboard.resource;

//import com.wallboard.wallboard.pbx.PBX;

import com.RingBoard.wallboard.pbx.PBX;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Resource findByName(String name);

    void deleteByName(String name);

    void deleteById(Long id);

    List<Resource> findByType(String type);

    List<Resource> findByNameContainingIgnoreCase(String search);
    void deleteByPbx(PBX pbx);
    List<Resource> findByPbx(PBX pbx);
}
