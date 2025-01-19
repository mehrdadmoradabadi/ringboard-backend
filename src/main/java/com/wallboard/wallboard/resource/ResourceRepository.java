package com.wallboard.wallboard.resource;

import com.wallboard.wallboard.pbx.PBX;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Resource findByName(String name);

    void deleteByName(String name);

    void deleteById(Long id);

    void deleteByPbx(PBX pbx);

    void deleteByPbxId(Long pbxId);

    Resource findByPbxAndName(PBX pbx, String name);

    Resource findByPbxIdAndName(Long pbxId, String name);

    List<Resource> findByType(String type);

    List<Resource> findByMetadata(String key, Object value);

    List<Resource> findByNameContainingIgnoreCase(String search);
}
