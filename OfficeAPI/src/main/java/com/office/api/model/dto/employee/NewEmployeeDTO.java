package com.office.api.model.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record NewEmployeeDTO(
        @NotBlank
        String name,
        @NotBlank
        String username,
        @CPF
        String cpf,
        @Email
        String email,
        @NotBlank
        String password) {
}
