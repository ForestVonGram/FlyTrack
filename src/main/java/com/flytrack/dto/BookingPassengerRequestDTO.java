package com.flytrack.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPassengerRequestDTO {
    @NotNull(message = "El vuelo es obligatorio")
    private Long flightId;
    @NotBlank(message = "La clase de reserva es obligatoria")
    private String bookingClass;
    @NotEmpty(message = "Debe haber al menos un pasajero en la reserva")
    @Valid
    private List<PassengerBookingRequestDTO> passengers; // Contains seat and baggages
}
