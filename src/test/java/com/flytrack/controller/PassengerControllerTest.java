package com.flytrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flytrack.dto.PassengerRequestDTO;
import com.flytrack.dto.PassengerResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.GlobalExceptionHandler;
import com.flytrack.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PassengerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PassengerService passengerService;

    @BeforeEach
    void setUp() {
        PassengerController controller = new PassengerController(passengerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createPassenger_Success() throws Exception {
        PassengerRequestDTO request = new PassengerRequestDTO("Ana", "Lopez", "ana@mail.com", "CC123", "3001234567");
        PassengerResponseDTO response = new PassengerResponseDTO(1L, "Ana", "Lopez", "ana@mail.com");

        when(passengerService.createPassenger(any(PassengerRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Ana"));

        verify(passengerService).createPassenger(any(PassengerRequestDTO.class));
    }

    @Test
    void updatePassenger_Success() throws Exception {
        PassengerRequestDTO request = new PassengerRequestDTO("Ana", "Lopez", "ana@mail.com", "CC123", "3001234567");
        PassengerResponseDTO response = new PassengerResponseDTO(1L, "Ana", "Lopez", "ana@mail.com");

        when(passengerService.updatePassenger(eq(1L), any(PassengerRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/passengers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ana@mail.com"));

        verify(passengerService).updatePassenger(eq(1L), any(PassengerRequestDTO.class));
    }

    @Test
    void getPassengerById_Success() throws Exception {
        PassengerResponseDTO response = new PassengerResponseDTO(1L, "Ana", "Lopez", "ana@mail.com");
        when(passengerService.getPassengerById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/passengers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Lopez"));

        verify(passengerService).getPassengerById(1L);
    }

    @Test
    void getAllPassengers_Success() throws Exception {
        PassengerResponseDTO response = new PassengerResponseDTO(1L, "Ana", "Lopez", "ana@mail.com");
        when(passengerService.getAllPassengers()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(passengerService).getAllPassengers();
    }

    @Test
    void createPassenger_BusinessException_Returns400() throws Exception {
        PassengerRequestDTO request = new PassengerRequestDTO("Ana", "Lopez", "ana@mail.com", "CC123", "3001234567");
        when(passengerService.createPassenger(any(PassengerRequestDTO.class)))
                .thenThrow(new BusinessException("Email ya registrado"));

        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(passengerService).createPassenger(any(PassengerRequestDTO.class));
    }
}

