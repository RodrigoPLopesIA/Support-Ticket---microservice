package com.rodrigolopes.ms.support_ticket.dto;

import java.util.Map;

import org.hibernate.query.results.ResultBuilder;

public record ErrorResponseDTO(String path, String message, int status, Map<String, String> errors) {

    public ErrorResponseDTO(String path, String message, int status) {
        this(path, message, status, null);
    }

}
