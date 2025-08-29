package com.rodrigolopes.ms.support_ticket.dto;

import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.validations.ValidEnum;

import jakarta.validation.constraints.NotBlank;

public record RequestTicketDTO(@NotBlank(message = "Title must not be empty") String title,
        @NotBlank(message = "Description must not be empty") String description, 
        @ValidEnum(enumClass = TicketStatus.class, message = "Status must be one of: OPEN, CLOSED, PENDING") String status) {

}
