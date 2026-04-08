package com.flytrack.mapper;

import com.flytrack.dto.BookingRequestDTO;
import com.flytrack.dto.BookingResponseDTO;
import com.flytrack.dto.BookingSummaryDTO;
import com.flytrack.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PassengerMapper.class, FlightMapper.class})
public interface BookingMapper {

    @Mapping(target = "passenger.id", source = "passengerId")
    @Mapping(target = "flight.id", source = "flightId")
    Booking toEntity(BookingRequestDTO dto);

    BookingResponseDTO toResponseDTO(Booking entity);

    BookingSummaryDTO toSummaryDTO(Booking entity);
}

