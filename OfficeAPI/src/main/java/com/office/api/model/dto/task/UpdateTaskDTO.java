package com.office.api.model.dto.task;

import jakarta.validation.constraints.NotBlank;

public record UpdateTaskDTO(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotBlank
        String deadline) {
}
