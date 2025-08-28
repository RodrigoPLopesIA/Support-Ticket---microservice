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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.entities.SupportTicket;
import com.rodrigolopes.ms.support_ticket.enums.TicketStatus;
import com.rodrigolopes.ms.support_ticket.services.SupportTicketService;

import jakarta.persistence.EntityNotFoundException;

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

        @Test
        @DisplayName("Should return 200 when calling the show endpoint with valid ID")
        public void testShowWithValidId() throws Exception {

                var request = get("/tickets/{id}", supportTicket.getId())
                                .contentType(MediaType.APPLICATION_JSON);

                BDDMockito.given(this.supportTicketService.getById(supportTicket.getId()))
                                .willReturn(responseTicketDTO);

                mockMvc.perform(request)
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(supportTicket.getId().toString()))
                                .andExpect(jsonPath("$.title").value(supportTicket.getTitle()))
                                .andExpect(jsonPath("$.description").value(supportTicket.getDescription()))
                                .andExpect(jsonPath("$.status").value(supportTicket.getStatus().name()));

        }

        @Test
        @DisplayName("Should return 404 when calling the show endpoint with invalid ID")
        public void testShowWithInvalidId() throws Exception {
                var invalidId = UUID.randomUUID();
                var request = get("/tickets/{id}", invalidId)
                                .contentType(MediaType.APPLICATION_JSON);

                BDDMockito.given(this.supportTicketService.getById(invalidId))
                                .willThrow(new EntityNotFoundException("Ticket not found"));

                mockMvc.perform(request)
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 201 when calling the store endpoint with valid data")
        public void testStoreWithValidData() throws Exception {
                var request = post("/tickets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body);
                BDDMockito.given(this.supportTicketService.create(Mockito.any(RequestTicketDTO.class)))
                                .willReturn(responseTicketDTO);

                mockMvc.perform(request)
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(supportTicket.getId().toString()))
                                .andExpect(jsonPath("$.title").value(supportTicket.getTitle()))
                                .andExpect(jsonPath("$.description").value(supportTicket.getDescription()))
                                .andExpect(jsonPath("$.status").value(supportTicket.getStatus().name()));

        }

        @Test
        @DisplayName("should return 400 when calling the store endpoint with duplicated title")
        public void testStoreWithDuplicatedTitle() throws Exception {
                var request = post("/tickets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body);
                BDDMockito.given(this.supportTicketService.create(Mockito.any(RequestTicketDTO.class)))
                                .willThrow(new IllegalArgumentException("A ticket with this title already exists."));
                mockMvc.perform(request)
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.path").value("/tickets"))
                                .andExpect(jsonPath("$.message").value("A ticket with this title already exists."))
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.errors").isEmpty());

        }

        @Test
        @DisplayName("should return 400 when calling the store endpoint with invalid data")
        public void testStoreWithInvalidData() throws Exception {
                var invalidRequestBody = "{}";
                var request = post("/tickets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequestBody);
                mockMvc.perform(request)
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.path").value("/tickets"))
                                .andExpect(jsonPath("$.message").value("Invalid arguments!"))
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.errors").isNotEmpty())
                                .andExpect(jsonPath("$.errors.title").value("Title must not be empty"))
                                .andExpect(jsonPath("$.errors.description").value("Description must not be empty"));
        }

        @Test
        @DisplayName("should return 200 when calling the update endpoint with valid data and ID")
        public void testUpdateWithValidDataAndId() throws Exception {
                var request = put("/tickets/{id}", supportTicket.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body);
                BDDMockito.given(this.supportTicketService.update(Mockito.eq(supportTicket.getId()),
                                Mockito.any(RequestTicketDTO.class)))
                                .willReturn(responseTicketDTO);
                mockMvc.perform(request)
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(supportTicket.getId().toString()))
                                .andExpect(jsonPath("$.title").value(supportTicket.getTitle()))
                                .andExpect(jsonPath("$.description").value(supportTicket.getDescription()))
                                .andExpect(jsonPath("$.status").value(supportTicket.getStatus().name()));
        }

        @Test
        @DisplayName("should return 404 when calling the update endpoint with valid data and invalid ID")
        public void testUpdateWithValidDataAndInvalidId() throws Exception {
                var invalidId = UUID.randomUUID();
                var request = put("/tickets/{id}", invalidId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body);
                BDDMockito.given(this.supportTicketService.update(Mockito.eq(invalidId),
                                Mockito.any(RequestTicketDTO.class)))
                                .willThrow(new EntityNotFoundException("Ticket not found"));
                mockMvc.perform(request)
                                .andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.path").value("/tickets/" + invalidId))
                                .andExpect(jsonPath("$.message").value("Ticket not found"))
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.errors").isEmpty());
        }

        @Test
        @DisplayName("should return 400 when calling the update endpoint with invalid data")
        public void testUpdateWithInvalidData() throws Exception {
                var invalidRequestBody = "{}";
                var request = put("/tickets/{id}", supportTicket.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequestBody);
                mockMvc.perform(request)
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.path").value("/tickets/" + supportTicket.getId()))
                                .andExpect(jsonPath("$.message").value("Invalid arguments!"))
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.errors").isNotEmpty())
                                .andExpect(jsonPath("$.errors.title").value("Title must not be empty"))
                                .andExpect(jsonPath("$.errors.description").value("Description must not be empty"));
        }
        
        @Test
        @DisplayName("should return 400 when calling the update endpoint with duplicated title")
        public void testUpdateWithDuplicatedTitle() throws Exception {
                var request = put("/tickets/{id}", supportTicket.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body);
                BDDMockito.given(this.supportTicketService.update(Mockito.eq(supportTicket.getId()),
                                Mockito.any(RequestTicketDTO.class)))
                                .willThrow(new IllegalArgumentException("A ticket with this title already exists."));
                mockMvc.perform(request)
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.path").value("/tickets/" + supportTicket.getId()))
                                .andExpect(jsonPath("$.message").value("A ticket with this title already exists."))
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.errors").isEmpty());     
        }

}
