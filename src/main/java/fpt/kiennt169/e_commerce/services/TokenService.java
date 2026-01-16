package fpt.kiennt169.e_commerce.services;

import org.springframework.security.core.Authentication;

import java.util.Set;

public interface TokenService {

    /**
     * Generate an access token for a user
     */
    String generateToken(Long userId, String email, Set<String> roles);

    /**
     * Validate a token and return true if valid
     */
    boolean validateToken(String token);

    /**
     * Get Authentication object from token
     */
    Authentication getAuthenticationFromToken(String token);
}
