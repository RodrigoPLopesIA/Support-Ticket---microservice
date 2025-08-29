package com.rodrigolopes.ms.support_ticket.dto;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.rodrigolopes.ms.support_ticket.enums.EventEnum;

public record KafkaMessageDTO(EventEnum eventType, UUID ticketId, Instant timestamp, ResponseTicketDTO payload) {

}
