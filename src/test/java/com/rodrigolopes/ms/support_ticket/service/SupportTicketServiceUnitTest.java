package com.rodrigolopes.ms.support_ticket.service;

import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.entities.SupportTicket;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.mapper.TicketMapper;
import com.rodrigolopes.ms.support_ticket.repositories.TicketSupportRepository;
import com.rodrigolopes.ms.support_ticket.services.SupportTicketService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class SupportTicketServiceUnitTest {

    @InjectMocks
    private SupportTicketService supportTicketService;

    @Mock
    private TicketSupportRepository ticketSupportRepository;

    @Mock
    private TicketMapper ticketMapper;

    ResponseTicketDTO responseDto;
    SupportTicket supportTicket;
    RequestTicketDTO requestDto;

    @BeforeEach
    public void setup() {
        supportTicket = SupportTicket.builder()
                .id(UUID.randomUUID())
                .title("Issue with login")
                .description("Unable to login with correct credentials")
                .build();

        responseDto = new ResponseTicketDTO(
                supportTicket.getId(),
                supportTicket.getTitle(),
                supportTicket.getDescription(),
                TicketStatus.OPEN.name(),
                new Date().toString(),
                new Date().toString());

        requestDto = new RequestTicketDTO(
                supportTicket.getTitle(),
                supportTicket.getDescription(),
                TicketStatus.OPEN.name());
    }

    @Test
    @DisplayName("Should retrieve all support tickets successfully")
    public void testGetAllSupportTickets() {
        var pageable = Pageable.unpaged();
        var page = new PageImpl<>(List.of(supportTicket), PageRequest.of(1, 10), 10);
        when(ticketSupportRepository.findAll(pageable)).thenReturn(page);
        when(ticketMapper.toResponseDto(supportTicket)).thenReturn(responseDto);

        Page<ResponseTicketDTO> result = supportTicketService.getAll(pageable);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getContent().get(0).id()).isEqualTo(supportTicket.getId());
        Assertions.assertThat(result.getContent().get(0).title()).isEqualTo("Issue with login");
        Assertions.assertThat(result.getContent().get(0).description()).isEqualTo("Unable to login with correct credentials");
    }

    @Test
    @DisplayName("Should retrieve a support ticket by ID successfully")
    public void testGetSupportTicketById() {
        var id = UUID.randomUUID();

        Mockito.when(ticketSupportRepository.findById(id)).thenReturn(Optional.of(supportTicket));
        Mockito.when(ticketMapper.toResponseDto(supportTicket)).thenReturn(responseDto);

        var result = supportTicketService.getById(id);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(supportTicket.getId());
        Assertions.assertThat(result.title()).isEqualTo("Issue with login");
        Assertions.assertThat(result.description()).isEqualTo("Unable to login with correct credentials");

        Mockito.verify(ticketSupportRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when ticket not found by ID")
    public void testGetSupportTicketById_NotFound() {
        var id = UUID.randomUUID();
        Mockito.when(ticketSupportRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> supportTicketService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ticket not found with id: " + id);
        Mockito.verify(ticketSupportRepository, Mockito.times(1)).findById(id);

    }

    @Test
    @DisplayName("Should create a support ticket successfully")
    public void testCreateSupportTicket() {

        Mockito.when(ticketMapper.toEntity(requestDto)).thenReturn(supportTicket);
        Mockito.when(ticketMapper.toResponseDto(supportTicket)).thenReturn(responseDto);
        Mockito.when(ticketSupportRepository.existsByTitle(Mockito.anyString())).thenReturn(false);
        Mockito.when(ticketSupportRepository.save(Mockito.any(SupportTicket.class))).thenReturn(supportTicket);

        ResponseTicketDTO result = supportTicketService.create(requestDto);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(supportTicket.getId());
        Assertions.assertThat(result.title()).isEqualTo("Issue with login");
        Assertions.assertThat(result.description()).isEqualTo("Unable to login with correct credentials");

        Mockito.verify(ticketSupportRepository, Mockito.times(1))
                .save(Mockito.any(SupportTicket.class));
    }

    @Test
    @DisplayName("Should throw exception when creating a ticket with duplicate title")
    public void testCreateSupportTicket_DuplicateTitle() {
        Mockito.when(ticketSupportRepository.existsByTitle(Mockito.anyString())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> supportTicketService.create(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A ticket with this title already exists.");

        Mockito.verify(ticketSupportRepository, Mockito.never())
                .save(Mockito.any(SupportTicket.class));
    }

    @Test
    @DisplayName("Should update a support ticket successfully")
    public void testUpdateSupportTicket() {
        var id = UUID.randomUUID();

        Mockito.when(ticketSupportRepository.findById(id)).thenReturn(Optional.of(supportTicket));
        Mockito.when(ticketSupportRepository.existsByTitleAndIdNot(requestDto.title(), id)).thenReturn(false);
        Mockito.when(ticketSupportRepository.save(supportTicket)).thenReturn(supportTicket);

        Mockito.when(ticketMapper.toResponseDto(supportTicket)).thenReturn(responseDto);

        var result = supportTicketService.update(id, requestDto);

        Assertions.assertThat(result).isNotNull();

        Mockito.verify(ticketSupportRepository, Mockito.times(1)).findById(id);
        Mockito.verify(ticketSupportRepository, Mockito.times(1)).save(supportTicket);
    }

    @Test
    @DisplayName("Should throw exception when updating a non-existent ticket")
    public void testUpdateSupportTicket_NotFound() {
        var id = UUID.randomUUID();
        Mockito.when(ticketSupportRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> supportTicketService.update(id, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ticket not found with id: " + id);

        Mockito.verify(ticketSupportRepository, Mockito.times(1)).findById(id);
        Mockito.verify(ticketSupportRepository, Mockito.never()).save(supportTicket);
    }

    @Test
    @DisplayName("Should throw exception when updating a ticket with duplicate title")
    public void testUpdateSupportTicket_DuplicateTitle() {
        var id = UUID.randomUUID();
        Mockito.when(ticketSupportRepository.findById(id)).thenReturn(Optional.of(supportTicket));
        Mockito.when(ticketSupportRepository.existsByTitleAndIdNot(requestDto.title(), id)).thenReturn(true);

        Assertions.assertThatThrownBy(() -> supportTicketService.update(id, requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A ticket with this title already exists in another record.");

        Mockito.verify(ticketSupportRepository, Mockito.times(1)).findById(id);
        Mockito.verify(ticketSupportRepository, Mockito.never()).save(supportTicket);
    }

    @Test
    @DisplayName("Should delete a support ticket successfully")
    public void testDeleteSupportTicket() {
        var id = UUID.randomUUID();
        Mockito.when(ticketSupportRepository.existsById(id)).thenReturn(true);

        supportTicketService.delete(id);

        Mockito.verify(ticketSupportRepository, Mockito.times(1)).deleteById(id);

    }

    @Test
    @DisplayName("Should throw exception when deleting a non-existent ticket")
    public void testDeleteSupportTicket_NotFound() {
        var id = UUID.randomUUID();
        Mockito.when(ticketSupportRepository.existsById(id)).thenReturn(false);
        Assertions.assertThatThrownBy(() -> supportTicketService.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ticket not found with id: " + id);

        Mockito.verify(ticketSupportRepository, Mockito.never()).deleteById(id);
    }
}
