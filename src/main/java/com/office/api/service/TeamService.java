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

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyService companyService;
    private final EmployeeService employeeService;

    public TeamService(TeamRepository teamRepository, EmployeeRepository employeeRepository, CompanyService companyService, EmployeeService employeeService) {
        this.teamRepository = teamRepository;
        this.employeeRepository = employeeRepository;
        this.companyService = companyService;
        this.employeeService = employeeService;
    }

    public void newTeam(NewTeamDTO data, JwtAuthenticationToken token) {
        Employee manager = employeeService.getEmployee(token.getName());

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
        Employee manager = employeeService.getEmployee(token.getName());

        Optional<Team> optionalTeam = teamRepository.findById(id);
        if(optionalTeam.isEmpty() || !manager.getCompany().equals(optionalTeam.get().getCompany()))
            throw new NullTeamException();
        Team team = optionalTeam.get();

        Set<Team> usedData = teamRepository.findByName(data.name());
        if(usedData.stream().anyMatch(teamValue -> !teamValue.getId().equals(id)))
            throw new UsedDataException();

        Set<Employee> employeesToAdd = this.filterToAdd(data.to_add(), team);
        Set<Employee> employeesToRemove = this.filterToRemove(data.to_remove(), team);

        team.setName(data.name());
        team.getMembers().addAll(employeesToAdd);
        team.getMembers().removeAll(employeesToRemove);
        teamRepository.save(team);
    }
    public void deleteTeam(Long id, JwtAuthenticationToken token) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        Employee manager = employeeService.getEmployee(token.getName());

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
        Employee manager = employeeService.getEmployee(token.getName());

        Set<Team> teams = manager.getProject().getTeams();
        return TeamDTO.toDTOList(teams);
    }

    private Set<Employee> filterToAdd(Set<String> data, Team team) {
        Set<Employee> toAdd = new HashSet<>();
        data.forEach(username -> {
            Optional<Employee> optionalEmployee = employeeRepository.findByUsername(username);
            if(optionalEmployee.isEmpty() || !optionalEmployee.get().getCompany().equals(team.getCompany()))
                throw new NullEmployeeException();

            Employee employee = optionalEmployee.get();
            if(employee.getTeam() != null)
                throw new InvalidEmployeeException("Already on a team");
            else if(employee.getRole().equals(Role.MANAGER))
                throw new InvalidEmployeeException("Manager can not be part of the team");

            employee.setTeam(team);
            toAdd.add(employee);
        });
        return toAdd;
    }
    private Set<Employee> filterToRemove(Set<String> data, Team team) {
        Set<Employee> toRemove = new HashSet<>();
        data.forEach(username -> {
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(NullEmployeeException::new);
            if(!employee.getTeam().equals(team))
                throw new InvalidEmployeeException("Does not exist in the team");

            employee.setTeam(null);
            toRemove.add(employee);
        });
        return toRemove;
    }
}