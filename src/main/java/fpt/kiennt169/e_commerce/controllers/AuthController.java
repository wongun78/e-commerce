package fpt.kiennt169.e_commerce.controllers;

import fpt.kiennt169.e_commerce.dtos.ApiResponse;
import fpt.kiennt169.e_commerce.dtos.auth.AuthResponse;
import fpt.kiennt169.e_commerce.dtos.auth.LoginRequest;
import fpt.kiennt169.e_commerce.dtos.auth.RegisterRequest;
import fpt.kiennt169.e_commerce.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }

    @PostMapping("/register")
    @Operation(
        summary = "User registration", 
        description = "Register new customer account with strong password validation. " +
                     "Password must be at least 8 characters with uppercase, lowercase, digit, and special character."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.register(request)));
    }
}
