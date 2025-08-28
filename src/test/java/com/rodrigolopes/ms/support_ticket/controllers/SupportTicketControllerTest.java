package com.rodrigolopes.ms.support_ticket.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.catalina.connector.Request;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.entities.SupportTicket;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.services.SupportTicketService;

@WebMvcTest(SupportTicketController.class)
public class SupportTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupportTicketService supportTicketService;

    SupportTicket supportTicket;
    ResponseTicketDTO responseTicketDTO;
    RequestTicketDTO requestTicketDTO;
    Page<ResponseTicketDTO> responseTicketDTOPage;

    String body;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        supportTicket = SupportTicket.builder()
                .id(UUID.randomUUID())
                .title("Sample Ticket")
                .description("This is a sample ticket description.")
                .status(TicketStatus.OPEN)
                .build();
        responseTicketDTO = new ResponseTicketDTO(
                supportTicket.getId(),
                supportTicket.getTitle(),
                supportTicket.getDescription(),
                supportTicket.getStatus().name(),
                new Date().toString(),
                new Date().toString());

        requestTicketDTO = new RequestTicketDTO(
                supportTicket.getTitle(),
                supportTicket.getDescription(),
                supportTicket.getStatus().name());

        responseTicketDTOPage = new PageImpl<>(List.of(responseTicketDTO), PageRequest.of(0, 100),
                1);

        body = new ObjectMapper().writeValueAsString(requestTicketDTO);

    }

    @Test
    @DisplayName("Should return 200 when calling the index endpoint")
    public void testIndex() throws Exception {

        var request = get("/tickets")
                .contentType(MediaType.APPLICATION_JSON);

        BDDMockito.given(this.supportTicketService.getAll(Mockito.any(Pageable.class)))
                .willReturn(responseTicketDTOPage);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(supportTicket.getId().toString()))
                .andExpect(jsonPath("$.content[0].title").value(supportTicket.getTitle()))
                .andExpect(jsonPath("$.content[0].description").value(supportTicket.getDescription()))
                .andExpect(jsonPath("$.content[0].status").value(supportTicket.getStatus().name()));

    }

}
