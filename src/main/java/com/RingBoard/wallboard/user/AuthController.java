package com.RingBoard.wallboard.user;

import com.RingBoard.wallboard.user.dto.UserDto;
import com.RingBoard.wallboard.utils.ApiResponse;
import com.RingBoard.wallboard.utils.InvalidCredentialsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountLockedException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto.AuthResponse>> registerUser(@Valid @RequestBody UserDto.RegisterRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto.AuthResponse>> login(@Valid @RequestBody UserDto.LoginRequest request) throws AccountLockedException, InvalidCredentialsException {
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserDto.ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}