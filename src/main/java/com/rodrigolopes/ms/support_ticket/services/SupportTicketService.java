package com.rodrigolopes.ms.support_ticket.services;

import com.fasterxml.jackson.databind.DatabindException;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.mapper.TicketMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.repositories.TicketSupportRepository;


@Service
public class SupportTicketService {
    

    @Autowired
    private TicketSupportRepository ticketSupportRepository;

    @Autowired
    private TicketMapper ticketMapper;


    public ResponseTicketDTO createTicket(RequestTicketDTO requestTicketDTO) {
        
        var supportTicket = ticketMapper.toEntity(requestTicketDTO);

        var createdTicket = ticketSupportRepository.save(supportTicket);
        return ticketMapper.toResponseDto(createdTicket);

    }
}
