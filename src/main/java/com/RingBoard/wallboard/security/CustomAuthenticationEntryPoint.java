package com.RingBoard.wallboard.security;

import com.RingBoard.wallboard.utils.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message = "Authentication failed: ";
        if (authException instanceof BadCredentialsException) {
            message += "Invalid credentials provided";
        } else if (authException instanceof InsufficientAuthenticationException) {
            message += "Full authentication is required to access this resource";
        } else {
            message += authException.getMessage();
        }

        ApiResponse<String> apiResponse = ApiResponse.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}