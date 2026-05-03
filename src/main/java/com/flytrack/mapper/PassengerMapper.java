package com.flytrack.mapper;

import com.flytrack.dto.PassengerRequestDTO;
import com.flytrack.dto.PassengerResponseDTO;
import com.flytrack.dto.PassengerSummaryDTO;
import com.flytrack.dto.PassengerBookingResponseDTO;
import com.flytrack.model.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    Passenger toEntity(PassengerRequestDTO dto);

    PassengerResponseDTO toResponseDTO(Passenger entity);

    @Mapping(target = "fullName", expression = "java(entity.getFirstName() + \" \" + entity.getLastName())")
    PassengerSummaryDTO toSummaryDTO(Passenger entity);

    PassengerBookingResponseDTO toBookingResponseDTO(Passenger entity);
}

