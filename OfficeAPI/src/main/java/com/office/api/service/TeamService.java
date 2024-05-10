package com.office.api.service;

import com.office.api.exception.InvalidEmployeeException;
import com.office.api.exception.NullEmployeeException;
import com.office.api.exception.NullTeamException;
import com.office.api.exception.UsedDataException;
import com.office.api.model.Company;
import com.office.api.model.Employee;
import com.office.api.model.Team;
import com.office.api.model.dto.team.NewTeamDTO;
import com.office.api.model.dto.team.TeamDTO;
import com.office.api.model.dto.team.UpdateTeamDTO;
import com.office.api.model.enums.Role;
import com.office.api.repository.EmployeeRepository;
import com.office.api.repository.TeamRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyService companyService;

    public TeamService(TeamRepository teamRepository, EmployeeRepository employeeRepository, CompanyService companyService) {
        this.teamRepository = teamRepository;
        this.employeeRepository = employeeRepository;
        this.companyService = companyService;
    }

    public void newTeam(NewTeamDTO data, JwtAuthenticationToken token) {
        UUID managerId = UUID.fromString(token.getName());
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(NullEmployeeException::new);
        if(teamRepository.existsByName(data.name())) throw new UsedDataException();

        Team team = new Team(
                data.name(),
                manager.getCompany(),
                manager.getProject());
        team.setMembers(new HashSet<>());

        data.usernames().forEach(username -> {
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(NullEmployeeException::new);
            if(employee.getTeam() != null)
                throw new InvalidEmployeeException("Already on a team");
            else if(employee.getRole().equals(Role.MANAGER))
                throw new InvalidEmployeeException("Manager can not be part of the team");
            team.getMembers().add(employee);
            employee.setTeam(team);
        });

        teamRepository.save(team);
    }
    public void updateTeam (Long id, UpdateTeamDTO data, JwtAuthenticationToken token) {
        if(teamRepository.existsByName(data.name())) throw new UsedDataException();
        Optional<Team> optionalTeam = teamRepository.findById(id);
        UUID managerId = UUID.fromString(token.getName());
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(NullEmployeeException::new);

        if(optionalTeam.isEmpty() || !manager.getCompany().equals(optionalTeam.get().getCompany()))
            throw new NullTeamException();

        Team team = optionalTeam.get();
        team.setName(data.name());
        teamRepository.save(team);
    }
    public void deleteTeam(Long id, JwtAuthenticationToken token) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        UUID managerId = UUID.fromString(token.getName());
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(NullEmployeeException::new);

        if(optionalTeam.isEmpty() || !manager.getCompany().equals(optionalTeam.get().getCompany()))
            throw new NullTeamException();

        Team team = optionalTeam.get();
        teamRepository.delete(team);
    }
    public Set<TeamDTO> getAllTeams(JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());
        Set<Team> teams = company.getTeams();
        return TeamDTO.toDTOList(teams);
    }
    public Set<TeamDTO> getTeams(JwtAuthenticationToken token) {
        UUID managerId = UUID.fromString(token.getName());
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(NullEmployeeException::new);

        Set<Team> teams = manager.getProject().getTeams();
        return TeamDTO.toDTOList(teams);
    }
}