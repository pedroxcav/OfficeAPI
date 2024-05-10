package com.office.api.model.dto.team;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UpdateTeamDTO(
        @NotBlank String name,
        Set<@NotBlank String> to_add,
        Set<@NotBlank String> to_remove) {
}
