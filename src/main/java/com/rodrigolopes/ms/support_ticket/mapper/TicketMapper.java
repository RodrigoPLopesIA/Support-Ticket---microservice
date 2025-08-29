package com.rodrigolopes.ms.support_ticket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.entities.SupportTicket;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    SupportTicket toEntity(RequestTicketDTO dto);

    ResponseTicketDTO toResponseDto(SupportTicket entity);
    SupportTicket toEntity(ResponseTicketDTO dto);
    RequestTicketDTO toRequestDto(SupportTicket entity);
}
