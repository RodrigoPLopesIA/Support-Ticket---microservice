package com.rodrigolopes.ms.support_ticket.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodrigolopes.ms.support_ticket.entities.SupportTicket;

public interface TicketSupportRepository extends JpaRepository<SupportTicket, UUID> {
    

    Boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, UUID id);
}
