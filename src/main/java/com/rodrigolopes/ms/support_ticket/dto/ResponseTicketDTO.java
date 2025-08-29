package com.rodrigolopes.ms.support_ticket.dto;

import java.util.UUID;

public record ResponseTicketDTO(
    UUID id,
    String title,
    String description,
    String status,
    String createdAt,
    String updatedAt
) {

}
