package com.flytrack.service;

import com.flytrack.dto.AuthRequestDTO;
import com.flytrack.dto.AuthRegisterRequestDTO;
import com.flytrack.dto.AuthResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.model.User;
import com.flytrack.model.enums.Role;
import com.flytrack.model.enums.NotificationType;
import com.flytrack.repository.UserRepository;
import com.flytrack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;

    public AuthResponseDTO register(AuthRegisterRequestDTO request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("El email (username) ya está registrado.");
        }

        var user = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PASAJERO) // Default role, you can change logic later
                .build();

        repository.save(user);

        notificationService.createUserNotification(
                user.getId(),
                "Bienvenido a FlyTrack, " + user.getName() + "!",
                NotificationType.REGISTRO
        );

        var jwtToken = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = repository.findByUsername(request.getUsername())
                .orElseThrow(); // Checked by authentication anyway

        notificationService.createUserNotification(
                user.getId(),
                "Has iniciado sesión exitosamente en FlyTrack.",
                NotificationType.LOGIN
        );

        var jwtToken = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}
