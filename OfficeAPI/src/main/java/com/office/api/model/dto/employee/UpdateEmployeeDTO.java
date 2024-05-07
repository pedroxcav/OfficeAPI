package com.office.api.model.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmployeeDTO(
        @NotBlank
        String name,
        @NotBlank
        String username,
        @Email
        String email,
        @NotBlank
        String password) {
}
