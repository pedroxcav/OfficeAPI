package com.office.api.model.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record NewTeamDTO(
        @NotBlank
        String name,
        @NotEmpty
        Set<@NotBlank String> usernames) {
}
