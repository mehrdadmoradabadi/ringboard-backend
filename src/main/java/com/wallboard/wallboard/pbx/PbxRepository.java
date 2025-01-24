package com.wallboard.wallboard.pbx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PbxRepository extends JpaRepository<PBX, Integer> {
    PBX findByName(String name);



    void deleteByName(String name);

    List<PBX> findByNameContainingIgnoreCaseOrHostContainingIgnoreCaseOrPortContainingIgnoreCaseOrProtocolContainingIgnoreCaseOrUsernameContainingIgnoreCase(String search, String search1, String search2, String search3, String search4);

    PBX findByNameOrHostOrId(String name , String host, Integer id);
}
