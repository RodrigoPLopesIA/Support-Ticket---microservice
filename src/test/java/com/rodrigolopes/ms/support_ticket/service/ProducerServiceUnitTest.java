package com.rodrigolopes.ms.support_ticket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.rodrigolopes.ms.support_ticket.dto.KafkaMessageDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.enums.EventEnum;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.services.ProducerService;

@ExtendWith(MockitoExtension.class)
public class ProducerServiceUnitTest {

    @InjectMocks
    private ProducerService producerService;

    @Mock
    private KafkaTemplate<String, KafkaMessageDTO> kafkaTemplate;

    @Captor
    ArgumentCaptor<KafkaMessageDTO> captor;

    private String topic = "ticket-events";
    ResponseTicketDTO payload;
    KafkaMessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        payload = new ResponseTicketDTO(UUID.randomUUID(), "Issue with login", "Cannot login to my account",
                TicketStatus.OPEN.name(), new Date().toString(), new Date().toString());

        messageDTO = new KafkaMessageDTO(EventEnum.CREATED, payload.id(), Instant.now(), payload);
    }

    @Test
    @DisplayName("should call kafkaTemplate.send when sendMessage is invoked")
    void testSendMessage() {

        producerService.sendMessage(EventEnum.CREATED, payload);

        Mockito.verify(kafkaTemplate).send(eq(topic), captor.capture());
        Mockito.verify(kafkaTemplate).flush();
        KafkaMessageDTO sent = captor.getValue();

        assertEquals(EventEnum.CREATED, sent.eventType());
        assertEquals(payload.id(), sent.ticketId());
        assertEquals(payload, sent.payload());
        assertNotNull(sent.timestamp());
    }

}
