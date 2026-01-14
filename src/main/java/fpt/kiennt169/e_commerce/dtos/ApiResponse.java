package fpt.kiennt169.e_commerce.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "Standard API response wrapper")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Schema(description = "Response timestamp", example = "2026-01-13T10:30:00")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "HTTP status code", example = "200")
    private int status;

    @Schema(description = "Response message", example = "Operation successful")
    private String message;

    @Schema(description = "Response data payload")
    private T data;

    @Schema(description = "Validation or error details")
    private Object errors;

    @Schema(description = "Request path", example = "/api/v1/products")
    private String path;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(int status, T data, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, String path) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, Object errors, String path) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .errors(errors)
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> badRequest(String message, Object errors, String path) {
        return error(400, message, errors, path);
    }

    public static <T> ApiResponse<T> unauthorized(String message, String path) {
        return error(401, message, path);
    }

    public static <T> ApiResponse<T> forbidden(String message, String path) {
        return error(403, message, path);
    }

    public static <T> ApiResponse<T> notFound(String message, String path) {
        return error(404, message, path);
    }

    public static <T> ApiResponse<T> internalError(String message, String path) {
        return error(500, message, path);
    }
}
