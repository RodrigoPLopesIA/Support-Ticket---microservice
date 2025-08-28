package com.rodrigolopes.ms.support_ticket.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rodrigolopes.ms.support_ticket.services.SupportTicketService;

@WebMvcTest(SupportTicketController.class)
public class SupportTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupportTicketService supportTicketService;

    @Test
    @DisplayName("Should return 200 when calling the index endpoint")
    public void testIndex() throws Exception {

       var request = get("/tickets")
               .contentType(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
               .andExpect(status().isOk()).andExpect(content().string("hello world"));

    }

}
