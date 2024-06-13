package com.office.api.model.dto.project;

import jakarta.validation.constraints.NotBlank;

public record UpdateProjectDTO(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotBlank
        String manager_username,
        @NotBlank
        String deadline) {
}
