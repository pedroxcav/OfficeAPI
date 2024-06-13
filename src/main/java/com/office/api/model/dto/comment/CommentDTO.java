package com.office.api.model.dto.comment;

import com.office.api.model.Comment;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public record CommentDTO(Long id,
                         String content,
                         String posted_at,
                         String owner_username) {

    public static CommentDTO toDTO(Comment comment) {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                formatter.format(comment.getPostedAt()),
                comment.getOwner().getUsername());
    }
    public static List<CommentDTO> toDTOList(Set<Comment> comments) {
        var commentList = comments.stream()
                .map(CommentDTO::toDTO)
                .sorted(Comparator.comparing(CommentDTO::posted_at))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(commentList);
        return commentList;
    }
}
