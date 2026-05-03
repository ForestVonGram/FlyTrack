package com.flytrack.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightRequestDTO {

    @NotBlank(message = "El código de vuelo es obligatorio")
    @Size(max = 20, message = "El código de vuelo no debe exceder 20 caracteres")
    private String flightCode;

    @NotBlank(message = "El origen es obligatorio")
    @Size(max = 100, message = "El origen no debe exceder 100 caracteres")
    private String origin;

    @NotBlank(message = "El destino es obligatorio")
    @Size(max = 100, message = "El destino no debe exceder 100 caracteres")
    private String destination;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser futura")
    private LocalDateTime departureTime;

    @NotNull(message = "La fecha estimada de llegada es obligatoria")
    @Future(message = "La fecha estimada de llegada debe ser futura")
    private LocalDateTime estimatedArrivalTime;

    @NotBlank(message = "El estado es obligatorio")
    private String status;

    @Size(max = 10, message = "La puerta no debe exceder 10 caracteres")
    private String gate;

    @NotBlank(message = "La aerolínea es obligatoria")
    @Size(max = 100, message = "La aerolínea no debe exceder 100 caracteres")
    private String airline;
}

