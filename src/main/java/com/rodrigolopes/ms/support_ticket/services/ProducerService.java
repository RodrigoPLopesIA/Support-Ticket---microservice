package com.rodrigolopes.ms.support_ticket.services;

import java.util.Date;

import org.apache.kafka.shaded.io.opentelemetry.proto.trace.v1.Span.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rodrigolopes.ms.support_ticket.dto.KafkaMessageDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.enums.EventEnum;

@Service
public class ProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(EventEnum event, ResponseTicketDTO payload) {
        kafkaTemplate.send("ticket-events", new KafkaMessageDTO(event, payload.id(), new Date(), payload));
    }

}
