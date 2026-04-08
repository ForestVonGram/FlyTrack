package com.flytrack.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaggageRequestDTO {

    @NotNull
    private Long bookingId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal weight;
}

