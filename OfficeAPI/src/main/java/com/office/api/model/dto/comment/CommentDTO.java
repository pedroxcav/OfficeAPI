package com.office.api.model.dto.comment;

import com.office.api.model.Comment;

import java.util.Set;
import java.util.stream.Collectors;

public record CommentDTO() {
    public static CommentDTO toDTO(Comment comment) {
        return new CommentDTO();
    }
    public static Set<CommentDTO> toDTOList(Set<Comment> comments) {
        return comments.stream().map(CommentDTO::toDTO).collect(Collectors.toSet());
    }
}
