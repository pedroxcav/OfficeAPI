package com.office.api.service;

import com.office.api.exception.InvalidEmployeeException;
import com.office.api.exception.NullEmployeeException;
import com.office.api.exception.NullTeamException;
import com.office.api.exception.UsedDataException;
import com.office.api.model.Company;
import com.office.api.model.Employee;
import com.office.api.model.Project;
import com.office.api.model.Team;
import com.office.api.model.dto.team.NewTeamDTO;
import com.office.api.model.dto.team.TeamDTO;
import com.office.api.model.dto.team.UpdateTeamDTO;
import com.office.api.model.enums.Role;
import com.office.api.repository.EmployeeRepository;
import com.office.api.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private CompanyService companyService;
    @InjectMocks
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Creates Team Successfully")
    void newTeam_successful() {
        String username = "test username";
        var data = mock(NewTeamDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Project project = mock(Project.class);
        Company company = mock(Company.class);
        Employee manager = mock(Employee.class);
        Employee employee = mock(Employee.class);
        Set<String> usernames = new HashSet<>() {{add(username);}};

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(teamRepository.existsByName(data.name())).thenReturn(false);
        when(manager.getCompany()).thenReturn(company);
        when(manager.getProject()).thenReturn(project);
        when(data.usernames()).thenReturn(usernames);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(employee));
        when(employee.getTeam()).thenReturn(null);
        when(employee.getRole()).thenReturn(Role.EMPLOYEE);

        assertDoesNotThrow(() -> teamService.newTeam(data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, times(1)).existsByName(data.name());
        verify(employeeRepository, times(1)).findByUsername(username);
        verify(teamRepository, times(1)).save(any(Team.class));
    }
    @Test
    @DisplayName("Creates Team Unsuccessfully - Used Data")
    void newTeam_unsuccessful_case01() {
        String username = "test username";
        var data = mock(NewTeamDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(teamRepository.existsByName(data.name())).thenReturn(true);

        assertThrows(UsedDataException.class, () -> teamService.newTeam(data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, times(1)).existsByName(data.name());
        verify(employeeRepository, never()).findByUsername(username);
        verify(teamRepository, never()).save(any(Team.class));
    }
    @Test
    @DisplayName("Creates Team Unsuccessfully - Non existent Employee")
    void newTeam_unsuccessful_case02() {
        String username = "test username";
        var data = mock(NewTeamDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Project project = mock(Project.class);
        Company company = mock(Company.class);
        Employee manager = mock(Employee.class);
        Set<String> usernames = new HashSet<>() {{
            add(username);
        }};

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(teamRepository.existsByName(data.name())).thenReturn(false);
        when(manager.getCompany()).thenReturn(company);
        when(manager.getProject()).thenReturn(project);
        when(data.usernames()).thenReturn(usernames);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NullEmployeeException.class, () -> teamService.newTeam(data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, times(1)).existsByName(data.name());
        verify(employeeRepository, times(1)).findByUsername(username);
        verify(teamRepository, never()).save(any(Team.class));
    }
    @Test
    @DisplayName("Creates Team Unsuccessfully - Already on a Team")
    void newTeam_unsuccessful_case03() {
        String username = "test username";
        var data = mock(NewTeamDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Team team = mock(Team.class);
        Project project = mock(Project.class);
        Company company = mock(Company.class);
        Employee manager = mock(Employee.class);
        Employee employee = mock(Employee.class);
        Set<String> usernames = new HashSet<>() {{
            add(username);
        }};

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(teamRepository.existsByName(data.name())).thenReturn(false);
        when(manager.getCompany()).thenReturn(company);
        when(manager.getProject()).thenReturn(project);
        when(data.usernames()).thenReturn(usernames);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(employee));
        when(employee.getTeam()).thenReturn(team);

        assertThrows(InvalidEmployeeException.class, () -> teamService.newTeam(data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, times(1)).existsByName(data.name());
        verify(employeeRepository, times(1)).findByUsername(username);
        verify(teamRepository, never()).save(any(Team.class));
    }
    @Test
    @DisplayName("Creates Team Unsuccessfully - ")
    void newTeam_unsuccessful_case04() {
        String username = "test username";
        var data = mock(NewTeamDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Project project = mock(Project.class);
        Company company = mock(Company.class);
        Employee manager = mock(Employee.class);
        Employee employee = mock(Employee.class);
        Set<String> usernames = new HashSet<>() {{
            add(username);
        }};

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(teamRepository.existsByName(data.name())).thenReturn(false);
        when(manager.getCompany()).thenReturn(company);
        when(manager.getProject()).thenReturn(project);
        when(data.usernames()).thenReturn(usernames);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(employee));
        when(employee.getTeam()).thenReturn(null);
        when(employee.getRole()).thenReturn(Role.MANAGER);

        assertThrows(InvalidEmployeeException.class, () -> teamService.newTeam(data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, times(1)).existsByName(data.name());
        verify(employeeRepository, times(1)).findByUsername(username);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Update Team Successfully")
    void updateTeam_successful() {
        Long id = 1L;
        var data = mock(UpdateTeamDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Team team = mock(Team.class);
        Company company = mock(Company.class);
        Set<Team> usedData = new HashSet<>();
        String username01 = "test username01";
        String username02 = "test username02";
        Employee employee01 = mock(Employee.class);
        Employee employee02 = mock(Employee.class);
        Set<String> to_add = new HashSet<>() {{add(username01);}};
        Set<String> to_remove = new HashSet<>() {{add(username02);}};

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(teamRepository.findById(id)).thenReturn(Optional.of(team));
        when(manager.getCompany()).thenReturn(company);
        when(team.getCompany()).thenReturn(company);
        when(teamRepository.findByName(data.name())).thenReturn(usedData);
        when(data.to_add()).thenReturn(to_add);
        when(data.to_remove()).thenReturn(to_remove);
        when(employeeRepository.findByUsername(username01)).thenReturn(Optional.of(employee01));
        when(employeeRepository.findByUsername(username02)).thenReturn(Optional.of(employee02));
        when(employee01.getCompany()).thenReturn(company);
        when(employee01.getRole()).thenReturn(Role.EMPLOYEE);
        when(employee02.getTeam()).thenReturn(team);

        assertDoesNotThrow(() -> teamService.updateTeam(id, data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, times(1)).findById(id);
        verify(teamRepository, times(1)).findByName(data.name());
        verify(employeeRepository, times(1)).findByUsername(username01);
        verify(employeeRepository, times(1)).findByUsername(username02);
        verify(teamRepository, times(1)).save(any(Team.class));
    }
    @Test
    @DisplayName("Update Team Unsuccessfully - Non existent Team")
    void updateTeam_unsuccessful_case01() {
        Long id = 1L;
        var data = mock(UpdateTeamDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullTeamException.class, () -> teamService.updateTeam(id, data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, never()).save(any(Team.class));
    }
    @Test
    @DisplayName("Update Team Unsuccessfully - Used Data")
    void updateTeam_unsuccessful_case02() {
        Long id = 1L;
        var data = mock(UpdateTeamDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Team team = mock(Team.class);
        Company company = mock(Company.class);
        Set<Team> usedData = new HashSet<>() {{add(mock(Team.class));}};

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(teamRepository.findById(id)).thenReturn(Optional.of(team));
        when(manager.getCompany()).thenReturn(company);
        when(team.getCompany()).thenReturn(company);
        when(teamRepository.findByName(data.name())).thenReturn(usedData);

        assertThrows(UsedDataException.class, () -> teamService.updateTeam(id, data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, times(1)).findById(id);
        verify(teamRepository, times(1)).findByName(data.name());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Delete Team Successfully")
    void deleteTeam_successful() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Team team = mock(Team.class);
        Company company = mock(Company.class);
        Employee manager = mock(Employee.class);

        when(teamRepository.findById(id)).thenReturn(Optional.of(team));
        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(manager.getCompany()).thenReturn(company);
        when(team.getCompany()).thenReturn(company);

        assertDoesNotThrow(() -> teamService.deleteTeam(id, token));

        verify(teamRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, times(1)).delete(team);
    }
    @Test
    @DisplayName("Delete Team Unsuccessfully")
    void deleteTeam_unsuccessful() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Team team = mock(Team.class);

        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullTeamException.class, () -> teamService.deleteTeam(id, token));

        verify(teamRepository, times(1)).findById(id);
        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(teamRepository, never()).delete(team);
    }

    @Test
    @DisplayName("Get All Teams Successfully")
    void getAllTeams() {
        Team team = mock(Team.class);
        Company company = mock(Company.class);
        Project project = mock(Project.class);
        Employee manager = mock(Employee.class);
        var token = mock(JwtAuthenticationToken.class);
        Set<Team> teams = new HashSet<>() {{add(team);}};

        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(company.getTeams()).thenReturn(teams);
        when(team.getProject()).thenReturn(project);
        when(project.getManager()).thenReturn(manager);
        when(project.getDeadline()).thenReturn(LocalDate.now().plusDays(1L));
        when(team.getMembers()).thenReturn(new HashSet<>());

        Set<TeamDTO> teamDTOs = assertDoesNotThrow(() -> teamService.getAllTeams(token));

        assertEquals(1, teamDTOs.size());
        verify(companyService, times(1)).getCompany(token.getName());
    }

    @Test
    @DisplayName("Get Teams Successfully")
    void getTeams() {
        Team team = mock(Team.class);
        Project project = mock(Project.class);
        Employee manager = mock(Employee.class);
        var token = mock(JwtAuthenticationToken.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(manager.getProject()).thenReturn(project);
        when(project.getTeams()).thenReturn(Set.of(team));
        when(team.getProject()).thenReturn(project);
        when(team.getMembers()).thenReturn(new HashSet<>());
        when(project.getManager()).thenReturn(manager);
        when(project.getDeadline()).thenReturn(LocalDate.now().plusDays(1L));

        Set<TeamDTO> teamDTOs = assertDoesNotThrow(() -> teamService.getTeams(token));

        assertEquals(1, teamDTOs.size());
        verify(employeeService, times(1)).getEmployee(token.getName());
    }
}