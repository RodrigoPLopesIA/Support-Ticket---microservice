package com.rodrigolopes.ms.support_ticket.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestTicketDTO(@NotBlank String title, @NotBlank String description, String status) {

}
