package com.rodrigolopes.ms.support_ticket.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rodrigolopes.ms.support_ticket.dto.KafkaMessageDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.enums.EventEnum;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProducerService {



    @Autowired
    private KafkaTemplate<String, KafkaMessageDTO> kafkaTemplate;

    public void sendMessage(EventEnum event, ResponseTicketDTO payload) {
        kafkaTemplate.send("ticket-events", new KafkaMessageDTO(event, payload.id(), Instant.now(), payload));
        kafkaTemplate.flush();
        log.info("Published event={} for ticketId={} to topic={}", event, payload.id(), "ticket-events");
    }

}
