package com.project.riverbed.shared.response;

public record ApiResponse<T>(
        int status,
        T data,
        String message
) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, data, message);
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, null, message);
    }
}