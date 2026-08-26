package com.krushiadhaar.auth.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshExpiration", 604800000L);
    }

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("John Doe"); req.setPhone("1234567890"); req.setPassword("pass"); req.setRole("FARMER");
        
        when(userRepository.existsByPhone("1234567890")).thenReturn(false);
        when(roleRepository.findByName("FARMER")).thenReturn(Optional.of(new Role(1, "FARMER")));
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse res = authService.register(req);

        assertNotNull(res.getAccessToken());
        assertNotNull(res.getRefreshToken());
        verify(userRepository).save(any(User.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void register_DuplicatePhoneFails() {
        RegisterRequest req = new RegisterRequest();
        req.setPhone("1234567890");
        when(userRepository.existsByPhone("1234567890")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> authService.register(req));
        assertEquals("VALIDATION_ERROR", ex.getCode());
    }

    @Test
    void login_Success() {
        LoginRequest req = new LoginRequest();
        req.setPhone("1234567890"); req.setPassword("pass");

        User user = new User(); user.setPhone("1234567890");
        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse res = authService.login(req);

        assertNotNull(res);
        assertEquals("jwt-token", res.getAccessToken());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refresh_SuccessAndRotation() {
        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("old-refresh-token");

        User user = new User(); user.setPhone("123");
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setExpiryDate(LocalDateTime.now().plusDays(1)); // valid
        
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(rt));
        when(jwtService.generateToken(any())).thenReturn("new-jwt-token");

        AuthResponse res = authService.refresh(req);

        assertNotNull(res);
        assertEquals("new-jwt-token", res.getAccessToken());
        assertNotNull(res.getRefreshToken());
        // Verify old token is deleted (rotation)
        verify(refreshTokenRepository).delete(rt);
        // Verify new token is saved
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refresh_ExpiredFails() {
        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("old-refresh-token");

        RefreshToken rt = new RefreshToken();
        rt.setExpiryDate(LocalDateTime.now().minusDays(1)); // expired
        
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(rt));

        assertThrows(ApiException.class, () -> authService.refresh(req));
        // Verify expired token is deleted
        verify(refreshTokenRepository).delete(rt);
    }

    @Test
    void logout_Success() {
        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("some-token");

        RefreshToken rt = new RefreshToken();
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(rt));

        authService.logout(req);

        verify(refreshTokenRepository).delete(rt);
    }
}
