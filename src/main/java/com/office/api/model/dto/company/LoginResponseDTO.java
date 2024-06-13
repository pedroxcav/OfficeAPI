package com.office.api.model.dto.company;

public record LoginResponseDTO(String token, Long expires_in) {
}
