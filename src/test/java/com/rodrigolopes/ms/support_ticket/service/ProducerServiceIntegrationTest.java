package com.rodrigolopes.ms.support_ticket.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.rodrigolopes.ms.support_ticket.dto.KafkaMessageDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.enums.EventEnum;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.services.ProducerService;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(topics = {
        "ticket-events" }, partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers", brokerProperties = {
                "auto.create.topics.enable=true"
        })

@Slf4j
public class ProducerServiceIntegrationTest {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    ResponseTicketDTO payload;
    KafkaMessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        payload = new ResponseTicketDTO(UUID.randomUUID(), "Issue with login", "Cannot login to my account",
                TicketStatus.OPEN.name(), new Date().toString(), new Date().toString());
        messageDTO = new KafkaMessageDTO(EventEnum.CREATED, payload.id(), Instant.now(), payload);
    }

    @Test
    void testProducerSendsMessage() {
        var consumer = this.createConsumer();

        producerService.sendMessage(EventEnum.CREATED, payload);

        ConsumerRecords<String, KafkaMessageDTO> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(7));

        assertThat(records).hasSize(1);
        assertThat(records.iterator().next().value().payload()).isEqualTo(payload);
        assertThat(records.iterator().next().value().ticketId()).isEqualTo(payload.id());

        consumer.close();
    }

    Consumer<String, KafkaMessageDTO> createConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("ticket-group", "true", embeddedKafkaBroker);

        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, KafkaMessageDTO> consumer = new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(), new JsonDeserializer<>(KafkaMessageDTO.class))
                .createConsumer();

        embeddedKafkaBroker.consumeFromEmbeddedTopics(consumer, "ticket-events");
        
        return consumer;
    }
}