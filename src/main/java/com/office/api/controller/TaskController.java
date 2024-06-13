package com.office.api.controller;

import com.office.api.model.dto.task.NewTaskDTO;
import com.office.api.model.dto.task.TaskDTO;
import com.office.api.model.dto.task.UpdateTaskDTO;
import com.office.api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Void> newTask(@RequestBody @Valid NewTaskDTO data, JwtAuthenticationToken token) {
        taskService.newTask(data, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable Long id,
                                           @RequestBody @Valid UpdateTaskDTO data,
                                           JwtAuthenticationToken token) {
        taskService.updateTask(id, data, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, JwtAuthenticationToken token) {
        taskService.deleteTask(id, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping
    public ResponseEntity<Set<TaskDTO>> getTasks(JwtAuthenticationToken token) {
        Set<TaskDTO> tasks = taskService.getTasks(token);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id, JwtAuthenticationToken token) {
        TaskDTO task = taskService.getTask(id, token);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }
}
