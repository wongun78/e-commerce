package fpt.kiennt169.e_commerce.services.impl;

import fpt.kiennt169.e_commerce.dtos.auth.AuthResponse;
import fpt.kiennt169.e_commerce.dtos.auth.LoginRequest;
import fpt.kiennt169.e_commerce.entities.User;
import fpt.kiennt169.e_commerce.services.AuthService;
import fpt.kiennt169.e_commerce.services.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = tokenService.generateToken(user.getId(), user.getEmail(), Set.of(user.getRole()));

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.of(
                token,
                jwtExpiration,
                AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .roles(Set.of(user.getRole()))
                        .build()
        );
    }
}
