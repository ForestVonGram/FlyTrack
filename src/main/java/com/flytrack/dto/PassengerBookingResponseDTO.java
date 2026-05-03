package com.flytrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerBookingResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String identityDocument;
    private String phoneNumber;
    private String seatNumber;
    private List<BaggageSummaryDTO> baggages;
}

