package com.office.api.model.dto.project;

import com.office.api.model.Project;

import java.util.Set;
import java.util.stream.Collectors;

public record ProjectDTO() {

    public static ProjectDTO toDTO(Project project) {
        return new ProjectDTO();
    }

    public static Set<ProjectDTO> toDTOList(Set<Project> projects) {
        return projects.stream().map(ProjectDTO::toDTO).collect(Collectors.toSet());
    }
}
