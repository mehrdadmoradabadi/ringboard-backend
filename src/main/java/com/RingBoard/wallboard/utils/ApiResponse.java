package com.RingBoard.wallboard.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String responseCode; // Custom response codes like "200", "400", etc.
    private String message;      // Response message
    private T result;            // Actual payload (data)

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(ApiResponseCode.SUCCESS, "Successful", result);
    }

    public static <T> ApiResponse<T> success(T result, String message) {
        return new ApiResponse<>(ApiResponseCode.SUCCESS, message, result);
    }

      public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(ApiResponseCode.NOT_FOUND, message, null);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(ApiResponseCode.BAD_REQUEST, message, null);
    }
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(ApiResponseCode.UNAUTHORIZED, message, null);
    }
    public static <T> ApiResponse<T> internalError(String message) {
        return new ApiResponse<>(ApiResponseCode.SERVER_ERROR, message, null);
    }
}

