package com.flytrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private PassengerSummaryDTO passenger;
    private FlightSummaryDTO flight;
    private String seatNumber;
    private String bookingClass;
    private String status;
    private LocalDateTime bookingDate;
}

