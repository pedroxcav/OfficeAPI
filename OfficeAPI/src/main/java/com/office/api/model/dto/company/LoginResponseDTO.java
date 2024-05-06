package com.office.api.model.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDTO(String token,
                               @JsonProperty("expires_in")
                               Long expiresIn) {
}
