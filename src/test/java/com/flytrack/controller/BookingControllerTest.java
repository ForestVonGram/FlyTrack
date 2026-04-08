package com.flytrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flytrack.dto.BookingRequestDTO;
import com.flytrack.dto.BookingResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.GlobalExceptionHandler;
import com.flytrack.service.BookingService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        BookingController controller = new BookingController(bookingService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createBooking_Success() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO(1L, 2L, "12A", "ECONOMY");
        BookingResponseDTO response = new BookingResponseDTO(10L, null, null, "12A", "ECONOMY", "CONFIRMED", LocalDateTime.now());

        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.seatNumber").value("12A"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(bookingService).createBooking(any(BookingRequestDTO.class));
    }

    @Test
    void cancelBooking_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/10/cancel"))
                .andExpect(status().isNoContent());

        verify(bookingService).cancelBooking(10L);
    }

    @Test
    void getBookingById_Success() throws Exception {
        BookingResponseDTO response = new BookingResponseDTO(10L, null, null, "12A", "ECONOMY", "CONFIRMED", LocalDateTime.now());
        when(bookingService.getBookingById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/bookings/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.bookingClass").value("ECONOMY"));

        verify(bookingService).getBookingById(10L);
    }

    @Test
    void getBookingsByPassenger_Success() throws Exception {
        BookingResponseDTO response = new BookingResponseDTO(10L, null, null, "12A", "ECONOMY", "CONFIRMED", LocalDateTime.now());
        when(bookingService.getBookingsByPassenger(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/bookings/passenger/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));

        verify(bookingService).getBookingsByPassenger(1L);
    }

    @Test
    void getBookingsByFlight_Success() throws Exception {
        BookingResponseDTO response = new BookingResponseDTO(10L, null, null, "12A", "ECONOMY", "CONFIRMED", LocalDateTime.now());
        when(bookingService.getBookingsByFlight(2L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/bookings/flight/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));

        verify(bookingService).getBookingsByFlight(2L);
    }

    @Test
    void createBooking_BusinessException_Returns400() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO(1L, 2L, "12A", "ECONOMY");

        when(bookingService.createBooking(any(BookingRequestDTO.class)))
                .thenThrow(new BusinessException("Vuelo sin cupos"));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(bookingService).createBooking(any(BookingRequestDTO.class));
    }
}

