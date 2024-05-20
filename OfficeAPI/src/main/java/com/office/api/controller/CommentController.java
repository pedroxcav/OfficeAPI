package com.office.api.controller;

import com.office.api.model.dto.comment.CommentDTO;
import com.office.api.model.dto.comment.NewCommentDTO;
import com.office.api.model.dto.comment.UpdateCommentDTO;
import com.office.api.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> newComment(
            @PathVariable Long id,
            @RequestBody @Valid NewCommentDTO data,
            JwtAuthenticationToken token) {
        commentService.newComment(id, data, token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCommentDTO data,
            JwtAuthenticationToken token) {
        commentService.updateComment(id, data, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, JwtAuthenticationToken token) {
        commentService.deleteComment(id, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Set<CommentDTO>> getAllComments(@PathVariable Long id, JwtAuthenticationToken token) {
        Set<CommentDTO> allComments = commentService.getAllComments(id, token);
        return ResponseEntity.status(HttpStatus.OK).body(allComments);
    }
    @GetMapping
    public ResponseEntity<Set<CommentDTO>> getComments(JwtAuthenticationToken token) {
        Set<CommentDTO> comments = commentService.getComments(token);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }
}
