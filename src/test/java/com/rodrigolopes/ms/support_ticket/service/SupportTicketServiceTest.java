package com.rodrigolopes.ms.support_ticket.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.entities.SupportTicket;
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
    @Test
    @DisplayName("Should create a support ticket successfully")
    public void testCreateSupportTicket() {
        
        SupportTicket supportTicket = SupportTicket.builder()
                .title("Issue with login")
                .description("Unable to login with correct credentials")
                .build();
        RequestTicketDTO requestDto = ticketMapper.toRequestDto(supportTicket);
        Mockito.when(ticketSupportRepository.save(supportTicket)).thenReturn(supportTicket);


        var result = supportTicketService.createTicket(requestDto);


        Assertions.assertThat(result).isNotNull();

        
    }
}
