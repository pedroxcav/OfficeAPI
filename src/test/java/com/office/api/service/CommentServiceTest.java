package com.office.api.service;

import com.office.api.exception.NullCommentException;
import com.office.api.exception.NullTaskException;
import com.office.api.model.*;
import com.office.api.model.dto.comment.CommentDTO;
import com.office.api.model.dto.comment.NewCommentDTO;
import com.office.api.model.dto.comment.UpdateCommentDTO;
import com.office.api.repository.CommentRepository;
import com.office.api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Successfully")
    void newComment_successful() {
        Long id = 1L;
        Task task = mock(Task.class);
        Team team = mock(Team.class);
        var data = mock(NewCommentDTO.class);
        Project project = mock(Project.class);
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employee.getTeam()).thenReturn(team);
        when(team.getProject()).thenReturn(project);
        when(task.getProject()).thenReturn(project);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(employeeService.getEmployee(token.getName())).thenReturn(employee);

        commentService.newComment(id, data, token);

        verify(commentRepository, times(1)).save(any());
        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Create Unsuccessfully - Non existent Task")
    void newComment_unsuccessful_case01() {
        Long id = 1L;
        var data = mock(NewCommentDTO.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullTaskException.class, () -> commentService.newComment(id, data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Create Unsuccessfully - Isn't its Project")
    void newComment_unsuccessful_case02() {
        Long id = 1L;
        Task task = mock(Task.class);
        Team team = mock(Team.class);
        var data = mock(NewCommentDTO.class);
        Project projectOne = mock(Project.class);
        Project projectTwo = mock(Project.class);
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employee.getTeam()).thenReturn(team);
        when(team.getProject()).thenReturn(projectOne);
        when(task.getProject()).thenReturn(projectTwo);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(employeeService.getEmployee(token.getName())).thenReturn(employee);

        assertThrows(NullTaskException.class, () -> commentService.newComment(id, data, token));
        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Update Successfully")
    void updateComment_successful() {
        Long id = 1L;
        Comment comment = mock(Comment.class);
        Set<Comment> comments = Set.of(comment);
        var data = mock(UpdateCommentDTO.class);
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employee.getComments()).thenReturn(comments);
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(employeeService.getEmployee(token.getName())).thenReturn(employee);

        commentService.updateComment(id, data, token);

        verify(comment, times(1)).setContent(any());
        verify(commentRepository, times(1)).save(any());
        verify(commentRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
    }
    @Test
    @DisplayName("Update Unsuccessfully - Non existent Comment")
    void updateComment_unsuccessful_case01() {
        Long id = 1L;
        var data = mock(UpdateCommentDTO.class);
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(employee);
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullCommentException.class, () -> commentService.updateComment(id, data, token));

        verify(commentRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
    }
    @Test
    @DisplayName("Update Unsuccessfully - Isn't its Comment")
    void updateComment_unsuccessful_case02() {
        Long id = 1L;
        Comment comment = mock(Comment.class);
        Set<Comment> comments = new HashSet<>();
        var data = mock(UpdateCommentDTO.class);
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employee.getComments()).thenReturn(comments);
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(employeeService.getEmployee(token.getName())).thenReturn(employee);

        assertThrows(NullCommentException.class, () -> commentService.updateComment(id, data, token));

        verify(commentRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteComment_successful() {
        Long id = 1L;
        Comment comment = mock(Comment.class);
        Set<Comment> comments = Set.of(comment);
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employee.getComments()).thenReturn(comments);
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(employeeService.getEmployee(token.getName())).thenReturn(employee);

        commentService.deleteComment(id, token);

        verify(commentRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(commentRepository, times(1)).delete(any());
    }
    @Test
    @DisplayName("Delete Unsuccessfully - Non existent Comment")
    void deleteComment_unsuccessful_case01() {
        Long id = 1L;
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(employee);
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullCommentException.class, () -> commentService.deleteComment(id, token));

        verify(commentRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
    }
    @Test
    @DisplayName("Delete Unsuccessfully - Isn't its Comment")
    void deleteComment_unsuccessful_case02() {
        Long id = 1L;
        Comment comment = mock(Comment.class);
        Set<Comment> comments = new HashSet<>();
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employee.getComments()).thenReturn(comments);
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(employeeService.getEmployee(token.getName())).thenReturn(employee);

        assertThrows(NullCommentException.class, () -> commentService.deleteComment(id, token));

        verify(commentRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
    }

    @Test
    @DisplayName("Get All Comments Successfully")
    void getAllComments_successful() {
        Long id = 1L;
        Task task = mock(Task.class);
        Comment comment = mock(Comment.class);
        Project project = mock(Project.class);
        Set<Comment> comments = Set.of(comment);
        Employee manager = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);


        when(task.getComments()).thenReturn(comments);
        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(Set.of(task));
        when(comment.getOwner()).thenReturn(mock(Employee.class));
        when(comment.getPostedAt()).thenReturn(LocalDateTime.now());
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(employeeService.getEmployee(token.getName())).thenReturn(manager);

        Set<CommentDTO> allComments = commentService.getAllComments(id, token);

        assertEquals(1, allComments.size());

        verify(taskRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
    }
    @Test
    @DisplayName("Get All Comments Unsuccessfully - Non existent Task")
    void getAllComments_unsuccessful_case01() {
        Long id = 1L;
        Employee manager = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        when(employeeService.getEmployee(token.getName())).thenReturn(manager);

        assertThrows(NullTaskException.class, () -> commentService.getAllComments(id, token));

        verify(taskRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
    }
    @Test
    @DisplayName("Get All Comments Unsuccessfully - Isn't its Project's Task")
    void getAllComments_unsuccessful_case02() {
        Long id = 1L;
        Task task = mock(Task.class);
        Project project = mock(Project.class);
        Employee manager = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(new HashSet<>());
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(employeeService.getEmployee(token.getName())).thenReturn(manager);

        assertThrows(NullTaskException.class, () -> commentService.getAllComments(id, token));

        verify(taskRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
    }

    @Test
    @DisplayName("Get Comments Successfully")
    void getComments() {
        Comment comment = mock(Comment.class);
        Employee employee = mock(Employee.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(employee.getComments()).thenReturn(Set.of(comment));
        when(comment.getOwner()).thenReturn(mock(Employee.class));
        when(comment.getPostedAt()).thenReturn(LocalDateTime.now());
        when(employeeService.getEmployee(token.getName())).thenReturn(employee);

        Set<CommentDTO> comments = commentService.getComments(token);

        assertEquals(1, comments.size());

        verify(employee, times(1)).getComments();
        verify(employeeService, times(1)).getEmployee(token.getName());
    }
}