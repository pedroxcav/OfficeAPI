package com.office.api.controller;

import com.office.api.model.dto.project.NewProjectDTO;
import com.office.api.model.dto.project.ProjectDTO;
import com.office.api.model.dto.project.UpdateProjectDTO;
import com.office.api.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Void> newProject(@RequestBody @Valid NewProjectDTO data, JwtAuthenticationToken token) {
        projectService.newProject(data, token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(@PathVariable Long id, @RequestBody @Valid UpdateProjectDTO data, JwtAuthenticationToken token) {
        projectService.updateProject(id, data, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, JwtAuthenticationToken token) {
        projectService.deleteProject(id, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping
    public ResponseEntity<Set<ProjectDTO>> getAllProjects(JwtAuthenticationToken token) {
        Set<ProjectDTO> projects = projectService.getAllProjects(token);
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id, JwtAuthenticationToken token) {
        ProjectDTO project = projectService.getProject(id, token);
        return ResponseEntity.status(HttpStatus.OK).body(project);
    }
}
