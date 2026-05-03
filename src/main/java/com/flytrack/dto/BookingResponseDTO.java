package com.flytrack.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private FlightSummaryDTO flight;
    private String bookingClass;
    private String status;
    private LocalDateTime bookingDate;
    private List<PassengerBookingResponseDTO> passengers;
}
