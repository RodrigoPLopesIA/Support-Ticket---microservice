package com.rodrigolopes.ms.support_ticket.services;

import com.fasterxml.jackson.databind.DatabindException;
import com.rodrigolopes.ms.support_ticket.enums.EventEnum;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.mapper.TicketMapper;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Producer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rodrigolopes.ms.support_ticket.dto.KafkaMessageDTO;
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

    @Autowired
    private ProducerService producerService;

    public Page<ResponseTicketDTO> getAll(Pageable pageable) {
        return ticketSupportRepository.findAll(pageable)
                .map(ticketMapper::toResponseDto);
    }

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

        var response = ticketMapper.toResponseDto(createdTicket);

        producerService
                .sendMessage(EventEnum.CREATED, response);
        return response;

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

        var response = ticketMapper.toResponseDto(updatedTicket);
        producerService
                .sendMessage(EventEnum.UPDATED, response);
        return response;
    }

    public void delete(UUID id) {
        var response = this.ticketSupportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id));

        producerService
                .sendMessage(EventEnum.DELETED, ticketMapper.toResponseDto(response));

        ticketSupportRepository.deleteById(id);
    }
}
