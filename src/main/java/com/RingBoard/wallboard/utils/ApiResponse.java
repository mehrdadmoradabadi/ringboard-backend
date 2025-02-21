package com.RingBoard.wallboard.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String responseCode;
    private String message;
    private T result;

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
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(ApiResponseCode.FORBIDDEN, message, null);
    }
}

