package com.flytrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BaggageResponseDTO;
import com.flytrack.exception.GlobalExceptionHandler;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.service.BaggageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BaggageControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BaggageService baggageService;

    @BeforeEach
    void setUp() {
        BaggageController controller = new BaggageController(baggageService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerBaggage_Success() throws Exception {
        BaggageRequestDTO request = new BaggageRequestDTO(10L, new BigDecimal("21.5"));
        BaggageResponseDTO response = new BaggageResponseDTO(1L, "TRK-001", new BigDecimal("21.5"), "REGISTERED", null);

        when(baggageService.registerBaggage(any(BaggageRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/baggage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.trackingCode").value("TRK-001"))
                .andExpect(jsonPath("$.status").value("REGISTERED"));

        verify(baggageService).registerBaggage(any(BaggageRequestDTO.class));
    }

    @Test
    void updateStatus_Success() throws Exception {
        BaggageResponseDTO response = new BaggageResponseDTO(1L, "TRK-001", new BigDecimal("21.5"), "LOADED", null);

        when(baggageService.updateStatus(eq("TRK-001"), eq("LOADED"))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/baggage/TRK-001/estado")
                        .param("status", "LOADED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingCode").value("TRK-001"))
                .andExpect(jsonPath("$.status").value("LOADED"));

        verify(baggageService).updateStatus("TRK-001", "LOADED");
    }

    @Test
    void getBaggageByTrackingCode_Success() throws Exception {
        BaggageResponseDTO response = new BaggageResponseDTO(1L, "TRK-001", new BigDecimal("21.5"), "IN_TRANSIT", null);

        when(baggageService.getBaggageByTrackingCode("TRK-001")).thenReturn(response);

        mockMvc.perform(get("/api/v1/baggage/tracking/TRK-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingCode").value("TRK-001"))
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));

        verify(baggageService).getBaggageByTrackingCode("TRK-001");
    }

    @Test
    void reportLost_Success() throws Exception {
        BaggageResponseDTO response = new BaggageResponseDTO(1L, "TRK-001", new BigDecimal("21.5"), "LOST", null);

        when(baggageService.reportLost("TRK-001")).thenReturn(response);

        mockMvc.perform(patch("/api/v1/baggage/TRK-001/lost"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LOST"));

        verify(baggageService).reportLost("TRK-001");
    }

    @Test
    void getBaggageByTrackingCode_NotFound_Returns404() throws Exception {
        when(baggageService.getBaggageByTrackingCode("TRK-404"))
                .thenThrow(new ResourceNotFoundException("Equipaje no encontrado"));

        mockMvc.perform(get("/api/v1/baggage/tracking/TRK-404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(baggageService).getBaggageByTrackingCode("TRK-404");
    }
}

