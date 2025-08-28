package com.rodrigolopes.ms.support_ticket.service;

import static org.mockito.Mockito.when;

import java.util.Date;
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

import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.entities.SupportTicket;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.mapper.TicketMapper;
import com.rodrigolopes.ms.support_ticket.repositories.TicketSupportRepository;
import com.rodrigolopes.ms.support_ticket.services.SupportTicketService;

@ExtendWith(MockitoExtension.class)
public class SupportTicketServiceTest {

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
    @DisplayName("Should create a support ticket successfully")
    public void testCreateSupportTicket() {

        Mockito.when(ticketMapper.toEntity(requestDto)).thenReturn(supportTicket);
        Mockito.when(ticketMapper.toResponseDto(supportTicket)).thenReturn(responseDto);
        Mockito.when(ticketSupportRepository.existsByTitle(Mockito.anyString())).thenReturn(false);
        Mockito.when(ticketSupportRepository.save(Mockito.any(SupportTicket.class))).thenReturn(supportTicket);

        // Act
        ResponseTicketDTO result = supportTicketService.create(requestDto);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(supportTicket.getId());
        Assertions.assertThat(result.title()).isEqualTo("Issue with login");
        Assertions.assertThat(result.description()).isEqualTo("Unable to login with correct credentials");

        Mockito.verify(ticketSupportRepository, Mockito.times(1))
                .save(Mockito.any(SupportTicket.class));
    }

}
