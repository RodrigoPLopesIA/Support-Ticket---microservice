package com.rodrigolopes.ms.support_ticket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rodrigolopes.ms.support_ticket.dto.KafkaMessageDTO;

@Service
public class ProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        kafkaTemplate.send("ticket-events", message);
    }

}
