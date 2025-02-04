package com.RingBoard.wallboard.pbx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PbxRepository extends JpaRepository<PBX, Integer> {
    PBX findByName(String name);



    void deleteByName(String name);

    @Query("SELECT p FROM PBX p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.host) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.ariPort) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.amiPort) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.protocol) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.username) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<PBX> searchPBX(@Param("search") String search);

    PBX findByNameOrHostOrId(String name , String host, Integer id);
}
