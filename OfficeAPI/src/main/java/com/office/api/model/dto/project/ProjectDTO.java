package com.office.api.model.dto.project;

import com.office.api.model.Project;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

public record ProjectDTO(
        String name,
        String description,
        String manager_username,
        String deadline,
        boolean status) {

    public static ProjectDTO toDTO(Project project) {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new ProjectDTO(
                project.getName(),
                project.getDescription(),
                project.getManager().getUsername(),
                formatter.format(project.getDeadline()),
                project.isExpired());
    }

    public static Set<ProjectDTO> toDTOList(Set<Project> projects) {
        return projects.stream().map(ProjectDTO::toDTO).collect(Collectors.toSet());
    }
}
