package com.office.api.controller;

import com.office.api.model.dto.employee.*;
import com.office.api.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        LoginResponseDTO token = employeeService.login(data);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
    @PostMapping
    public ResponseEntity<Void> newEmployee(
            @RequestBody @Valid NewEmployeeDTO data,
            JwtAuthenticationToken token) {
        employeeService.newEmployee(data, token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping
    public ResponseEntity<Void> updateEmployee(
            @RequestBody @Valid UpdateEmployeeDTO data,
            JwtAuthenticationToken token) {
        employeeService.updateEmployee(data, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> removeEmployee(
            @PathVariable @NotBlank String username,
            JwtAuthenticationToken token) {
        employeeService.removeEmployee(username, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping
    public ResponseEntity<Set<EmployeeDTO>> getAllEmployees(JwtAuthenticationToken token) {
        Set<EmployeeDTO> allEmployees = employeeService.getAllEmployees(token);
        return ResponseEntity.status(HttpStatus.OK).body(allEmployees);
    }
    @GetMapping("/me")
    public ResponseEntity<EmployeeDTO> getEmployee(JwtAuthenticationToken token) {
        EmployeeDTO employee = employeeService.getEmployee(token);
        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }
}
