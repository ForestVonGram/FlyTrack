package com.flytrack.mapper;

import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BaggageResponseDTO;
import com.flytrack.dto.BaggageSummaryDTO;
import com.flytrack.model.Baggage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BaggageMapper {

    Baggage toEntity(BaggageRequestDTO dto);

    BaggageResponseDTO toResponseDTO(Baggage entity);

    BaggageSummaryDTO toSummaryDTO(Baggage entity);
}

