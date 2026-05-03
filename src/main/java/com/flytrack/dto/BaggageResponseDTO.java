package com.flytrack.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaggageResponseDTO {
    private Long id;
    private String trackingCode;
    private BigDecimal weight;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
