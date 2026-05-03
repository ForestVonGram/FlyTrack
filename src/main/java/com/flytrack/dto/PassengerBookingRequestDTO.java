package com.flytrack.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class PassengerBookingRequestDTO {
    @NotBlank(message = "Nombre es obligatorio")
    private String firstName;
    @NotBlank(message = "Apellido es obligatorio")
    private String lastName;
    @NotBlank(message = "Email es obligatorio")
    private String email;
    @NotBlank(message = "Documento de identidad es obligatorio")
    private String identityDocument;
    private String phoneNumber;
    @Size(max = 10, message = "El asiento no debe exceder 10 caracteres")
    private String seatNumber;
    @Valid
    private List<BaggageRequestDTO> baggages;
}
