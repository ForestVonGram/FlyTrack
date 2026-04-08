package com.flytrack.mapper;

import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BaggageResponseDTO;
import com.flytrack.model.Baggage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface BaggageMapper {

    @Mapping(target = "booking.id", source = "bookingId")
    Baggage toEntity(BaggageRequestDTO dto);

    BaggageResponseDTO toResponseDTO(Baggage entity);
}

