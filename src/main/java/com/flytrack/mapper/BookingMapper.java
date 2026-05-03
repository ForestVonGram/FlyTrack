package com.flytrack.mapper;

import com.flytrack.dto.BookingRequestDTO;
import com.flytrack.dto.BookingResponseDTO;
import com.flytrack.dto.BookingSummaryDTO;
import com.flytrack.model.Booking;
import com.flytrack.model.enums.BookingClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PassengerMapper.class, FlightMapper.class})
public interface BookingMapper {

    @Mapping(target = "flight.id", source = "flightId")
    Booking toEntity(BookingRequestDTO dto);

    BookingResponseDTO toResponseDTO(Booking entity);

    BookingSummaryDTO toSummaryDTO(Booking entity);

    default String map(BookingClass value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case ECONOMICA -> "ECONOMY";
            case EJECUTIVA -> "BUSINESS";
            case PRIMERA -> "FIRST";
            default -> value.name();
        };
    }
}
