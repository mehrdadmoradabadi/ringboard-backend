package com.RingBoard.wallboard.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


    User findByUsername(String username);
    User findByEmail(String email);
    User findBySessionToken(String sessionToken);
}
