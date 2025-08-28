package com.rodrigolopes.ms.support_ticket.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestTicketDTO(@NotBlank(message = "Title must not be empty") String title,
        @NotBlank(message = "Description must not be empty") String description, String status) {

}
