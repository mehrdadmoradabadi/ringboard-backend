package com.RingBoard.wallboard.user;

import com.RingBoard.wallboard.ugroup.Group;
import com.RingBoard.wallboard.user.dto.UserDto;
import com.RingBoard.wallboard.utils.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;


@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public User promoteToAdmin(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        user.setRole(UserRole.ADMIN);
        user.setUpdatedAt(ZonedDateTime.now());
        return userRepository.save(user);
    }
    private UserDto.UserResponse mapToResponse(User user) {
        UserDto.UserResponse response = new UserDto.UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setRole(String.valueOf(user.getRole()));
        response.setIsAccountLocked(user.getIsAccountLocked());
        response.setGroups(user.getGroups().stream().map(Group::getName).toList());
        return response;
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto.UserResponse> getAllUsers() {

        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public User getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    public boolean isSessionValid(String sessionToken) {
        User user = userRepository.findBySessionToken(sessionToken);
        if (user != null && user.getSessionExpiry() != null) {
            return user.getSessionExpiry().isAfter(ZonedDateTime.now());
        }
        return false;
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}