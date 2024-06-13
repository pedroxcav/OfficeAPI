package com.office.api.service;

import com.office.api.exception.NullCommentException;
import com.office.api.exception.NullTaskException;
import com.office.api.model.Comment;
import com.office.api.model.Employee;
import com.office.api.model.Task;
import com.office.api.model.dto.comment.CommentDTO;
import com.office.api.model.dto.comment.NewCommentDTO;
import com.office.api.model.dto.comment.UpdateCommentDTO;
import com.office.api.repository.CommentRepository;
import com.office.api.repository.TaskRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CommentService {
    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, EmployeeService employeeService) {
        this.taskRepository = taskRepository;
        this.employeeService = employeeService;
        this.commentRepository = commentRepository;
    }

    public void newComment(Long id, NewCommentDTO data, JwtAuthenticationToken token) {
        Employee employee = employeeService.getEmployee(token.getName());
        Task task = taskRepository.findById(id).orElseThrow(NullTaskException::new);

        if(!task.getProject().equals(employee.getTeam().getProject()))
            throw new NullTaskException();

        commentRepository.save(new Comment(data.content(), employee, task));
    }
    public void updateComment(Long id, UpdateCommentDTO data, JwtAuthenticationToken token) {
        Employee employee = employeeService.getEmployee(token.getName());
        Comment comment = commentRepository.findById(id).orElseThrow(NullCommentException::new);

        if(!employee.getComments().contains(comment))
            throw new NullCommentException();

        comment.setContent(data.content());

        commentRepository.save(comment);
    }
    public void deleteComment(Long id, JwtAuthenticationToken token) {
        Employee employee = employeeService.getEmployee(token.getName());
        Comment comment = commentRepository.findById(id).orElseThrow(NullCommentException::new);

        if(!employee.getComments().contains(comment))
            throw new NullCommentException();

        commentRepository.delete(comment);
    }
    public List<CommentDTO> getAllComments(Long id, JwtAuthenticationToken token) {
        Employee manager = employeeService.getEmployee(token.getName());
        Task task = taskRepository.findById(id).orElseThrow(NullTaskException::new);

        if(!manager.getProject().getTasks().contains(task))
            throw new NullTaskException();

        Set<Comment> comments = task.getComments();
        return CommentDTO.toDTOList(comments);
    }
    public List<CommentDTO> getComments(JwtAuthenticationToken token) {
        Employee employee = employeeService.getEmployee(token.getName());

        Set<Comment> comments = employee.getComments();
        return CommentDTO.toDTOList(comments);
    }
}
