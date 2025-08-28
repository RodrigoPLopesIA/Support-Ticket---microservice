package com.rodrigolopes.ms.support_ticket.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.services.SupportTicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/tickets")
public class SupportTicketController {

    @Autowired
    private SupportTicketService supportTicketService;

    @GetMapping()
    public ResponseEntity<Page<ResponseTicketDTO>> index(Pageable pageable) {
        var data = this.supportTicketService.getAll(pageable);
        ;
        return ResponseEntity.ok().body(data);
    }

}
