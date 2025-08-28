package com.rodrigolopes.ms.support_ticket.services;

import com.fasterxml.jackson.databind.DatabindException;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.mapper.TicketMapper;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.repositories.TicketSupportRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SupportTicketService {

    @Autowired
    private TicketSupportRepository ticketSupportRepository;

    @Autowired
    private TicketMapper ticketMapper;


    public ResponseTicketDTO getById(UUID id) {
        var supportTicket = ticketSupportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id));
        return ticketMapper.toResponseDto(supportTicket);
    }

    public ResponseTicketDTO create(RequestTicketDTO requestTicketDTO) {

        if (this.ticketSupportRepository.existsByTitle(requestTicketDTO.title())) {
            throw new IllegalArgumentException("A ticket with this title already exists.");
        }
        var supportTicket = ticketMapper.toEntity(requestTicketDTO);

        var createdTicket = ticketSupportRepository.save(supportTicket);
        return ticketMapper.toResponseDto(createdTicket);

    }

    public ResponseTicketDTO update(UUID id, RequestTicketDTO requestDto) {
        var existingTicket = ticketSupportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id));

        if (ticketSupportRepository.existsByTitleAndIdNot(requestDto.title(), id)) {
            throw new IllegalArgumentException("A ticket with this title already exists in another record.");
        }
        
        existingTicket.setTitle(requestDto.title());
        existingTicket.setDescription(requestDto.description());
        existingTicket.setStatus(TicketStatus.valueOf(requestDto.status()));

        var updatedTicket = ticketSupportRepository.save(existingTicket);

        return ticketMapper.toResponseDto(updatedTicket);
    }

    public void delete(UUID id) {

        if (!ticketSupportRepository.existsById(id)) {
            throw new EntityNotFoundException("Ticket not found with id: " + id);
        }

        ticketSupportRepository.deleteById(id);
    }
}
