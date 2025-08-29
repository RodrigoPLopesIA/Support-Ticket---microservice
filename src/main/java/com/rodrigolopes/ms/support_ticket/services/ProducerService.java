package com.rodrigolopes.ms.support_ticket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rodrigolopes.ms.support_ticket.dto.KafkaMessageDTO;

@Service
public class ProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(KafkaMessageDTO payload) {
        kafkaTemplate.send("ticket-events", payload);
    }

}
