package com.rodrigolopes.ms.support_ticket.dto;

import java.util.Date;
import java.util.UUID;

import com.rodrigolopes.ms.support_ticket.enums.EventEnum;

public record KafkaMessageDTO(EventEnum eventType, UUID ticketId, Date timestamp, ResponseTicketDTO payload) {

}
