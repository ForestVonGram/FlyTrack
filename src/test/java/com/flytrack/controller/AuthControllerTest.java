package com.flytrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flytrack.dto.AuthRequestDTO;
import com.flytrack.dto.AuthResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.GlobalExceptionHandler;
import com.flytrack.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(authService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void register_Success() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("user@example.com", "password123");
        AuthResponseDTO response = new AuthResponseDTO("jwt-token", "user@example.com", "PASAJERO");

        when(authService.register(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("PASAJERO"));

        verify(authService).register(any(AuthRequestDTO.class));
    }

    @Test
    void login_Success() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("user@example.com", "password123");
        AuthResponseDTO response = new AuthResponseDTO("jwt-token", "user@example.com", "PASAJERO");

        when(authService.login(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("PASAJERO"));

        verify(authService).login(any(AuthRequestDTO.class));
    }

    @Test
    void register_BusinessException_ResponseIncludesErrorMetadata() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("user@example.com", "password123");

        when(authService.register(any(AuthRequestDTO.class)))
                .thenThrow(new BusinessException("Usuario ya existe"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(authService).register(any(AuthRequestDTO.class));
    }

    @Test
    void login_AuthenticationException_ResponseIncludesErrorMetadata() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("user@example.com", "wrong-pass");

        when(authService.login(any(AuthRequestDTO.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));

        verify(authService).login(any(AuthRequestDTO.class));
    }

    @Test
    void register_UnexpectedException_ReturnsInternalServerError() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("user@example.com", "password123");

        when(authService.register(any(AuthRequestDTO.class)))
                .thenThrow(new RuntimeException("service failure"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("service failure"));

        verify(authService).register(any(AuthRequestDTO.class));
    }

    @Test
    void login_UnexpectedException_ReturnsInternalServerError() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("user@example.com", "password123");

        when(authService.login(any(AuthRequestDTO.class)))
                .thenThrow(new RuntimeException("service failure"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("service failure"));

        verify(authService).login(any(AuthRequestDTO.class));
    }
}
