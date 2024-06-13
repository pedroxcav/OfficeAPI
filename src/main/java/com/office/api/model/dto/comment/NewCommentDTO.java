package com.office.api.model.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record NewCommentDTO(
        @NotBlank
        String content) {
}
