package com.office.api.model.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentDTO(
        @NotBlank
        String content) {
}
