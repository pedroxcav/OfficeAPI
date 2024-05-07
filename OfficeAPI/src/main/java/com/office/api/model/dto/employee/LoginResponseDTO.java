package com.office.api.model.dto.employee;

public record LoginResponseDTO(String token,
                               Long expires_in) {
}
