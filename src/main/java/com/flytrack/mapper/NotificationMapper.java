package com.flytrack.mapper;

import com.flytrack.dto.NotificationResponseDTO;
import com.flytrack.model.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FlightMapper.class})
public interface NotificationMapper {

    NotificationResponseDTO toResponseDTO(Notification entity);

    List<NotificationResponseDTO> toResponseDTOList(List<Notification> entities);
}

