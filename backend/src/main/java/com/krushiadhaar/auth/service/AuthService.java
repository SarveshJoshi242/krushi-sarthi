package com.krushiadhaar.auth.service;

import com.krushiadhaar.common.exception.DomainException;
import com.krushiadhaar.auth.dto.*;
import com.krushiadhaar.auth.entity.RefreshToken;
import com.krushiadhaar.auth.repository.RefreshTokenRepository;
import com.krushiadhaar.common.exception.ApiException;
import com.krushiadhaar.security.CustomUserDetails;
import com.krushiadhaar.security.JwtService;
import com.krushiadhaar.user.entity.Role;
import com.krushiadhaar.user.entity.User;
import com.krushiadhaar.user.repository.RoleRepository;
import com.krushiadhaar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${JWT_REFRESH_EXPIRATION:604800000}")
    private long refreshExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ApiException("VALIDATION_ERROR", "Phone already registered");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("VALIDATION_ERROR", "Email already registered");
        }
        
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ApiException("VALIDATION_ERROR", "Invalid role"));

        User user = User.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .build();
        user.getRoles().add(role);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = UUID.randomUUID().toString();
        
        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
        );
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid credentials"));
                
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = UUID.randomUUID().toString();
        
        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());
        RefreshToken rt = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid or revoked refresh token"));

        if (rt.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(rt);
            throw new ApiException("UNAUTHORIZED", "Refresh token expired");
        }

        User user = rt.getUser();
        // Revoke old token to implement rotation
        refreshTokenRepository.delete(rt);

        // Issue new tokens
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newJwt = jwtService.generateToken(userDetails);
        String newRefreshToken = UUID.randomUUID().toString();
        
        saveRefreshToken(user, newRefreshToken);

        return AuthResponse.builder()
                .accessToken(newJwt)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(RefreshRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(refreshTokenRepository::delete);
    }

    private void saveRefreshToken(User user, String rawToken) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiryDate(LocalDateTime.now().plus(refreshExpiration, ChronoUnit.MILLIS))
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new DomainException("Failed to hash token", e);
        }
    }
}
