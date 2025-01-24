package com.wallboard.wallboard.user;

import com.wallboard.wallboard.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ApiResponse<List<User>> getAllUsers() {
        List<User> Users = userService.getAllUsers();
        return new ApiResponse<>(userService.getAllUsers());
    }

    @GetMapping("/save")
    public ApiResponse<String> saveUser() {
        User User = new User();
        User.setUsername("test2");
        User.setEmail("test2");
        User.setRole("test2");
        User.setHashedPassword("test22");
        userService.saveUser(User);
        return new ApiResponse<>("Done");
    }
}
