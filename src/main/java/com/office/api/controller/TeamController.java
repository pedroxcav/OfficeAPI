package com.office.api.controller;

import com.office.api.model.dto.team.NewTeamDTO;
import com.office.api.model.dto.team.TeamDTO;
import com.office.api.model.dto.team.UpdateTeamDTO;
import com.office.api.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<Void> newTeam(@RequestBody @Valid NewTeamDTO data, JwtAuthenticationToken token) {
        teamService.newTeam(data, token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTeam(@PathVariable Long id,
                                           @RequestBody @Valid UpdateTeamDTO data,
                                           JwtAuthenticationToken token) {
        teamService.updateTeam(id, data, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id, JwtAuthenticationToken token) {
        teamService.deleteTeam(id, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping
    public ResponseEntity<Set<TeamDTO>> getAllTeams(JwtAuthenticationToken token) {
        Set<TeamDTO> teams = teamService.getAllTeams(token);
        return ResponseEntity.status(HttpStatus.OK).body(teams);
    }
    @GetMapping("/project")
    public ResponseEntity<Set<TeamDTO>> getTeams(JwtAuthenticationToken token) {
        Set<TeamDTO> teams = teamService.getTeams(token);
        return ResponseEntity.status(HttpStatus.OK).body(teams);
    }
}
