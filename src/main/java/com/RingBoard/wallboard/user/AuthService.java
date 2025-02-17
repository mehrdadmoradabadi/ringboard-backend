package com.RingBoard.wallboard.user;

import com.RingBoard.wallboard.security.JwtTokenProvider;
import com.RingBoard.wallboard.ugroup.Group;
import com.RingBoard.wallboard.user.dto.UserDto;
import com.RingBoard.wallboard.utils.ApiResponse;
import com.RingBoard.wallboard.utils.InvalidCredentialsException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountLockedException;
import java.time.ZonedDateTime;

@Service
@Transactional
public class AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 15;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Value("${app.session.timeout}")
    private long sessionTimeout;

    private UserDto.UserResponse mapToResponse(User user) {
        UserDto.UserResponse response = new UserDto.UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setRole(String.valueOf(user.getRole()));
        response.setIsAccountLocked(user.getIsAccountLocked());
        if (user.getGroups() != null) {
            response.setGroups(user.getGroups().stream().map(Group::getName).toList());
        } else {
            response.setGroups(null);
        }

        return response;
    }
    @PostConstruct
    public void createDefaultAdmin() {
        if (userRepository.findByUsername("admin") == null) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@example.com");
            user.setHashedPassword(passwordEncoder.encode("admin123")); // Change the password after first login
            user.setRole(UserRole.ADMIN);
            user.setCreatedAt(ZonedDateTime.now());
            user.setUpdatedAt(ZonedDateTime.now());
            user.setIsAccountLocked(false);

            userRepository.save(user);
            System.out.println("Default admin user created: admin / admin123 /admin@example.com");
        }
    }
    public ApiResponse<UserDto.AuthResponse> registerUser(UserDto.RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setHashedPassword(passwordEncoder.encode(request.getPassword()));
        user.setGroups(null);
        user.setRole(UserRole.USER);
        user.setCreatedAt(ZonedDateTime.now());
        user.setUpdatedAt(ZonedDateTime.now());

        userRepository.save(user);

        return ApiResponse.success(new UserDto.AuthResponse(null,null, mapToResponse(user)));
    }

    public ApiResponse<String> logout() {
        SecurityContextHolder.clearContext();
        return ApiResponse.success("Logged out successfully");
    }

    public ApiResponse<UserDto.AuthResponse> login(UserDto.LoginRequest request) throws AccountLockedException, InvalidCredentialsException {
        User user = userRepository.findByUsername(request.getUsername());
        if (user != null && user.getIsAccountLocked() &&
                user.getLockTime().plusMinutes(LOCK_TIME_DURATION).isAfter(ZonedDateTime.now())) {
            throw new AccountLockedException("Account is locked. Please try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);


            if (user != null) {
                user.setFailedAttempts(0);
                user.setLockTime(null);
                user.setLastLogin(ZonedDateTime.now());
                userRepository.save(user);
            }

            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            assert user != null;
            return ApiResponse.success(new UserDto.AuthResponse(accessToken, refreshToken, mapToResponse(user)));

        } catch (BadCredentialsException e) {
            if (user != null) {
                if (user.getFailedAttempts() == null) {
                    user.setFailedAttempts(0);
                }

                user.setFailedAttempts(user.getFailedAttempts() + 1);

                if (user.getFailedAttempts() >= MAX_LOGIN_ATTEMPTS) {
                    user.setIsAccountLocked(true);
                    user.setLockTime(ZonedDateTime.now());
                    System.out.println("Account locked for user: "+ user.getUsername());
                }

                userRepository.save(user);
            }

            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    public ApiResponse<String> changePassword(UserDto.ChangePasswordRequest request) {
        // Get current authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(ZonedDateTime.now());
        userRepository.save(user);

        return ApiResponse.success("Password changed successfully");
    }
}