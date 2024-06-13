package com.office.api.model.dto.company;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank
        String name,
        @NotBlank
        String password) {
}
