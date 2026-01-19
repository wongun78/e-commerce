package fpt.kiennt169.e_commerce.services.impl;

import fpt.kiennt169.e_commerce.dtos.auth.AuthResponse;
import fpt.kiennt169.e_commerce.dtos.auth.LoginRequest;
import fpt.kiennt169.e_commerce.dtos.auth.RegisterRequest;
import fpt.kiennt169.e_commerce.entities.User;
import fpt.kiennt169.e_commerce.exceptions.BadRequestException;
import fpt.kiennt169.e_commerce.repositories.UserRepository;
import fpt.kiennt169.e_commerce.services.AuthService;
import fpt.kiennt169.e_commerce.services.TokenService;
import fpt.kiennt169.e_commerce.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageUtil messageUtil;

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

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Registration attempt for email: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException(messageUtil.getMessage("user.email.exists"));
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role("ROLE_CUSTOMER")
                .build();

        userRepository.save(newUser);
        log.info("User registered successfully: {}", newUser.getEmail());

        String token = tokenService.generateToken(newUser.getId(), newUser.getEmail(), Set.of(newUser.getRole()));

        return AuthResponse.of(
                token,
                jwtExpiration,
                AuthResponse.UserInfo.builder()
                        .id(newUser.getId())
                        .email(newUser.getEmail())
                        .fullName(newUser.getFullName())
                        .roles(Set.of(newUser.getRole()))
                        .build()
        );
    }
}
