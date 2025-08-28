package com.rodrigolopes.ms.support_ticket.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rodrigolopes.ms.support_ticket.entities.SupportTicket;
import com.rodrigolopes.ms.support_ticket.repositories.TicketSupportRepository;
import com.rodrigolopes.ms.support_ticket.services.SupportTicketService;

@ExtendWith(MockitoExtension.class)
public class SupportTicketServiceTest {
    

    @InjectMocks
    private SupportTicketService supportTicketService;

    @Mock
    private TicketSupportRepository ticketSupportRepository;


    @Test
    @DisplayName("Should create a support ticket successfully")
    public void testCreateSupportTicket() {
        

    }
}
