package com.flytrack.mapper;

import com.flytrack.dto.FlightRequestDTO;
import com.flytrack.dto.FlightResponseDTO;
import com.flytrack.dto.FlightSummaryDTO;
import com.flytrack.model.Flight;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FlightMapper {

    Flight toEntity(FlightRequestDTO dto);

    FlightResponseDTO toResponseDTO(Flight entity);

    FlightSummaryDTO toSummaryDTO(Flight entity);

    List<FlightResponseDTO> toResponseDTOList(List<Flight> entities);
}

