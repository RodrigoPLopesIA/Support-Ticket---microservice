package com.rodrigolopes.ms.support_ticket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.repositories.TicketSupportRepository;

@Service
public class SupportTicketService {
    

    @Autowired
    private TicketSupportRepository ticketSupportRepository;



    public ResponseTicketDTO createTicket(RequestTicketDTO requestTicketDTO) {
        

    }
}
