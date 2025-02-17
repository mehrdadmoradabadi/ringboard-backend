package com.RingBoard.wallboard.user;

import com.RingBoard.wallboard.user.dto.UserDto;
import com.RingBoard.wallboard.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Get all users")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserDto.UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));

    }

    @GetMapping("/save")
    public ResponseEntity<ApiResponse<String>> saveUser() {
        return ResponseEntity.ok(ApiResponse.success("Done"));
    }
}
