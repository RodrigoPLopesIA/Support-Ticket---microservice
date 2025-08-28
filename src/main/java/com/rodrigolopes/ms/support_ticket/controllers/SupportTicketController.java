package com.rodrigolopes.ms.support_ticket.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.rodrigolopes.ms.support_ticket.dto.RequestTicketDTO;
import com.rodrigolopes.ms.support_ticket.dto.ResponseTicketDTO;
import com.rodrigolopes.ms.support_ticket.services.SupportTicketService;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @GetMapping("/{id}")
    public ResponseEntity<ResponseTicketDTO> show(@PathVariable String id) {
        return ResponseEntity.ok().body(this.supportTicketService.getById(UUID.fromString(id)));
    }

    @PostMapping()
    public ResponseEntity<ResponseTicketDTO> create(@Valid @RequestBody RequestTicketDTO body) {
        var response = this.supportTicketService.create(body);
        var uri = UriComponentsBuilder.fromPath("/tickets/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri)
                .body(response);
    }

}
