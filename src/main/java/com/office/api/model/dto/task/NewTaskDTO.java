package com.office.api.model.dto.task;

import jakarta.validation.constraints.NotBlank;

public record NewTaskDTO(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotBlank
        String deadline) {
}
