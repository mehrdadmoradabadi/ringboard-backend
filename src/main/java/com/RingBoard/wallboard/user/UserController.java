package com.RingBoard.wallboard.user;

import com.RingBoard.wallboard.user.dto.UserDto;
import com.RingBoard.wallboard.utils.ApiResponse;
import com.RingBoard.wallboard.utils.IDNormalizer;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Get all users")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserDto.UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @PatchMapping("/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> promoteUser(@PathVariable String userId) {
        userId = IDNormalizer.normalize(userId);
        userService.promoteToAdmin(Integer.valueOf(userId));
        return ResponseEntity.ok(ApiResponse.success("User promoted successfully"));
    }

    @PatchMapping("/demote/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> demoteUser(@PathVariable String userId) {
        userId = IDNormalizer.normalize(userId);
        userService.demoteToUser(Integer.valueOf(userId));
        return ResponseEntity.ok(ApiResponse.success("User demoted successfully"));
    }
    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserDto.UserResponse>> getCurrentUserProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getCurrentUserProfile()));
    }

    @Operation(summary = "Check if session is valid", description = "Check if session is valid")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> findBySessionId(@PathVariable String sessionId) {
        return ResponseEntity.ok(ApiResponse.success(userService.isSessionValid(sessionId)));
    }

}
