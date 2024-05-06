package com.office.api.model.dto.team;

import com.office.api.model.Team;

import java.util.Set;
import java.util.stream.Collectors;

public record TeamDTO() {

    public static TeamDTO toDTO(Team team) {
        return new TeamDTO();
    }
    public static Set<TeamDTO> toDTOList(Set<Team> teams) {
        return teams.stream().map(TeamDTO::toDTO).collect(Collectors.toSet());
    }
}
