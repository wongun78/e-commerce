package fpt.kiennt169.e_commerce.services;

import fpt.kiennt169.e_commerce.dtos.auth.AuthResponse;
import fpt.kiennt169.e_commerce.dtos.auth.LoginRequest;
import fpt.kiennt169.e_commerce.dtos.auth.RegisterRequest;

public interface AuthService {

    /**
     * Authenticate user and return JWT token
     */
    AuthResponse login(LoginRequest request);

    /**
     * Register new user account
     */
    AuthResponse register(RegisterRequest request);
}
