package com.flytrack.controller;

import com.flytrack.dto.NotificationResponseDTO;
import com.flytrack.exception.GlobalExceptionHandler;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        NotificationController controller = new NotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllNotifications_Success() throws Exception {
        NotificationResponseDTO response = new NotificationResponseDTO(1L, "Puerta actualizada", "INFO", LocalDateTime.now(), false, null);
        when(notificationService.getPassengerNotifications(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/notifications/passenger/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].message").value("Puerta actualizada"));

        verify(notificationService).getPassengerNotifications(1L);
    }

    @Test
    void getUnreadNotifications_Success() throws Exception {
        NotificationResponseDTO response = new NotificationResponseDTO(2L, "Vuelo demorado", "WARNING", LocalDateTime.now(), false, null);
        when(notificationService.getUnreadPassengerNotifications(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/notifications/passenger/1/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(notificationService).getUnreadPassengerNotifications(1L);
    }

    @Test
    void markAsRead_Success() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/2/read"))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(2L);
    }

    @Test
    void markAsRead_NotFound_Returns404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Notificacion no encontrada"))
                .when(notificationService).markAsRead(404L);

        mockMvc.perform(patch("/api/v1/notifications/404/read"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(notificationService).markAsRead(404L);
    }
}
