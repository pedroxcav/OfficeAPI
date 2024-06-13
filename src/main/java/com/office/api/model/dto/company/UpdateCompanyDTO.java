package com.office.api.model.dto.company;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record UpdateCompanyDTO(
        @NotBlank
        String name,
        @CNPJ
        String cnpj,
        @NotBlank
        String password) {
}
