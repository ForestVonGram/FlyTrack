package com.flytrack.service.impl;

import com.flytrack.dto.AuthRequestDTO;
import com.flytrack.dto.AuthResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.model.User;
import com.flytrack.model.enums.Role;
import com.flytrack.repository.UserRepository;
import com.flytrack.security.JwtService;
import com.flytrack.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private AuthRequestDTO request;
    private User user;

    @BeforeEach
    void setUp() {
        request = new AuthRequestDTO("test@example.com", "password123");
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("encoded_password")
                .role(Role.PASAJERO)
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        AuthResponseDTO response = authService.register(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("test@example.com", response.getUsername());
        assertEquals("PASAJERO", response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists_ThrowsBusinessException() {
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                authService.register(request)
        );

        assertTrue(exception.getMessage().contains("ya está registrado"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("test@example.com", response.getUsername());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}

