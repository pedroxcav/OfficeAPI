package com.office.api.model.dto.team;

import jakarta.validation.constraints.NotBlank;

public record UpdateTeamDTO(@NotBlank String name) {
}
