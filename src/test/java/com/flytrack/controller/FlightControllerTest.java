package com.flytrack.controller;

import com.flytrack.dto.FlightRequestDTO;
import com.flytrack.dto.FlightResponseDTO;
import com.flytrack.exception.GlobalExceptionHandler;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        FlightController controller = new FlightController(flightService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createFlight_Success() throws Exception {
        FlightRequestDTO request = buildRequest();
        FlightResponseDTO response = buildResponse(1L, "ON_TIME", "A1");

        when(flightService.createFlight(any(FlightRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.flightCode").value("FL-100"));

        verify(flightService).createFlight(any(FlightRequestDTO.class));
    }

    @Test
    void updateFlight_Success() throws Exception {
        FlightRequestDTO request = buildRequest();
        FlightResponseDTO response = buildResponse(1L, "DELAYED", "B2");
        when(flightService.updateFlight(eq(1L), any(FlightRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELAYED"))
                .andExpect(jsonPath("$.gate").value("B2"));

        verify(flightService).updateFlight(eq(1L), any(FlightRequestDTO.class));
    }

    @Test
    void deleteFlight_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/flights/1"))
                .andExpect(status().isNoContent());

        verify(flightService).deleteFlight(1L);
    }

    @Test
    void getAllFlights_WithoutEstado_UsesGetAll() throws Exception {
        when(flightService.getAllFlights()).thenReturn(List.of(buildResponse(1L, "ON_TIME", "A1")));

        mockMvc.perform(get("/api/v1/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(flightService).getAllFlights();
    }

    @Test
    void getAllFlights_WithEstado_UsesFilterByStatus() throws Exception {
        when(flightService.getFlightsByStatus("ON_TIME")).thenReturn(List.of(buildResponse(1L, "ON_TIME", "A1")));

        mockMvc.perform(get("/api/v1/flights").param("estado", "ON_TIME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ON_TIME"));

        verify(flightService).getFlightsByStatus("ON_TIME");
    }

    @Test
    void searchFlights_Success() throws Exception {
        when(flightService.getFlightsByOriginAndDestination("BOG", "MDE"))
                .thenReturn(List.of(buildResponse(1L, "ON_TIME", "A1")));

        mockMvc.perform(get("/api/v1/flights/search")
                        .param("origen", "BOG")
                        .param("destino", "MDE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].origin").value("BOG"))
                .andExpect(jsonPath("$[0].destination").value("MDE"));

        verify(flightService).getFlightsByOriginAndDestination("BOG", "MDE");
    }

    @Test
    void changeStatus_Success() throws Exception {
        when(flightService.changeStatus(1L, "BOARDING")).thenReturn(buildResponse(1L, "BOARDING", "A1"));

        mockMvc.perform(patch("/api/v1/flights/1/estado").param("estado", "BOARDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOARDING"));

        verify(flightService).changeStatus(1L, "BOARDING");
    }

    @Test
    void updateGate_Success() throws Exception {
        when(flightService.updateGate(1L, "C3")).thenReturn(buildResponse(1L, "ON_TIME", "C3"));

        mockMvc.perform(patch("/api/v1/flights/1/puerta").param("puerta", "C3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gate").value("C3"));

        verify(flightService).updateGate(1L, "C3");
    }

    @Test
    void getFlightById_NotFound_Returns404() throws Exception {
        when(flightService.getFlightById(404L)).thenThrow(new ResourceNotFoundException("Vuelo no encontrado"));

        mockMvc.perform(get("/api/v1/flights/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(flightService).getFlightById(404L);
    }

    private FlightRequestDTO buildRequest() {
        LocalDateTime departure = LocalDateTime.now().plusHours(2);
        LocalDateTime arrival = LocalDateTime.now().plusHours(4);
        return new FlightRequestDTO("FL-100", "BOG", "MDE", departure, arrival, "ON_TIME", "A1", "FlyTrack");
    }

    private String buildRequestJson(FlightRequestDTO request) {
        return String.format(
                "{\"flightCode\":\"%s\",\"origin\":\"%s\",\"destination\":\"%s\",\"departureTime\":\"%s\",\"estimatedArrivalTime\":\"%s\",\"status\":\"%s\",\"gate\":\"%s\",\"airline\":\"%s\"}",
                request.getFlightCode(),
                request.getOrigin(),
                request.getDestination(),
                request.getDepartureTime(),
                request.getEstimatedArrivalTime(),
                request.getStatus(),
                request.getGate(),
                request.getAirline()
        );
    }

    private FlightResponseDTO buildResponse(Long id, String status, String gate) {
        LocalDateTime departure = LocalDateTime.now().plusHours(2);
        LocalDateTime arrival = LocalDateTime.now().plusHours(4);
        return new FlightResponseDTO(id, "FL-100", "BOG", "MDE", departure, arrival, status, gate, "FlyTrack");
    }
}
