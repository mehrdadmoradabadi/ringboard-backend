package com.RingBoard.wallboard.user;

import com.RingBoard.wallboard.utils.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void saveUser(User User) {
        userRepository.save(User);
    }

    public User findById(Integer id) { return userRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("User not found with ID: " + id)); }
    public User findByUsername(String username) { return userRepository.findByUsername(username); }
}
