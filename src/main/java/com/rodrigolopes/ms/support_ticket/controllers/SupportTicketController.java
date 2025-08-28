package com.rodrigolopes.ms.support_ticket.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/tickets")
public class SupportTicketController {

    @GetMapping()
    public ResponseEntity<String> index() {
        return ResponseEntity.ok().body("hello world");
    }

}
