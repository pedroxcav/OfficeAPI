package com.office.api.controller;

import com.office.api.model.dto.company.*;
import com.office.api.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        LoginResponseDTO login = companyService.login(data);
        return ResponseEntity.status(HttpStatus.OK).body(login);
    }
    @PostMapping
    public ResponseEntity<Void> newCompany(@RequestBody @Valid NewCompanyDTO data) {
        companyService.newCompany(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping
    public ResponseEntity<Void> updateCompany(@RequestBody @Valid UpdateCompanyDTO data, JwtAuthenticationToken token) {
        companyService.updateCompany(data, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping
    public ResponseEntity<Void> removeCompany(JwtAuthenticationToken token) {
        companyService.removeCompany(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping
    public ResponseEntity<CompanyDTO> getCompany(JwtAuthenticationToken token) {
        CompanyDTO data = companyService.getCompany(token);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}
