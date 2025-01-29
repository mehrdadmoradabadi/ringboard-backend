package com.wallboard.wallboard.user;

import com.wallboard.wallboard.utils.ApiResponse;
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

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> Users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    @GetMapping("/save")
    public ResponseEntity<ApiResponse<String>> saveUser() {
        try {
        User User = new User();
        User.setUsername("test2");
        User.setEmail("test2");
        User.setRole("test2");
        User.setHashedPassword("test22");
        userService.saveUser(User);
        return ResponseEntity.ok(ApiResponse.success("Done"));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
    }
    }
}
