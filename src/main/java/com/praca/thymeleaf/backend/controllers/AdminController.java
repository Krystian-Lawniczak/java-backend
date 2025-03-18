package com.praca.thymeleaf.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // Endpoint dostępny tylko dla roli ADMIN
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminAccess() {
        return "Dostęp do endpointu admina!";
    }
}
