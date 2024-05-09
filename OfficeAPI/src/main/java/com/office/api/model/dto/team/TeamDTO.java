package com.office.api.model.dto.team;

import com.office.api.model.Team;
import com.office.api.model.dto.employee.EmployeeDTO;
import com.office.api.model.dto.project.ProjectDTO;

import java.util.Set;
import java.util.stream.Collectors;

public record TeamDTO(
        Long id,
        String name,
        ProjectDTO project,
        Set<EmployeeDTO> members) {

    public static TeamDTO toDTO(Team team) {
        return new TeamDTO(
                team.getId(),
                team.getName(),
                ProjectDTO.toDTO(team.getProject()),
                EmployeeDTO.toDTOList(team.getMembers()));
    }
    public static Set<TeamDTO> toDTOList(Set<Team> teams) {
        return teams.stream().map(TeamDTO::toDTO).collect(Collectors.toSet());
    }
}
