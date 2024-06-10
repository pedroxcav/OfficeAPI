package com.office.api.service;

import com.office.api.exception.*;
import com.office.api.model.Company;
import com.office.api.model.Employee;
import com.office.api.model.Project;
import com.office.api.model.dto.project.NewProjectDTO;
import com.office.api.model.dto.project.ProjectDTO;
import com.office.api.model.dto.project.UpdateProjectDTO;
import com.office.api.model.enums.Role;
import com.office.api.repository.EmployeeRepository;
import com.office.api.repository.ProjectRepository;
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

class ProjectServiceTest {
    @Mock
    private CompanyService companyService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Project Successfully")
    void newProject_successful() {
        var token = mock(JwtAuthenticationToken.class);
        NewProjectDTO data = mock(NewProjectDTO.class);
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        Employee manager = new Employee("Test Name", "Test Username", "Test CPF",
                "Test Email", "Test Password", company);
        company.setEmployees(new HashSet<>(){{add(manager);}});
        Optional<Employee> optional = Optional.of(manager);
        manager.setRole(Role.EMPLOYEE);

        when(projectRepository.existsByName(data.name())).thenReturn(false);
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(data.deadline()).thenReturn("01/12/2030");
        when(employeeRepository.findByUsername(any())).thenReturn(optional);

        assertDoesNotThrow(() -> projectService.newProject(data, token));

        verify(projectRepository, times(1)).save(any());
        verify(projectRepository, times(1)).existsByName(data.name());
        verify(companyService, times(1)).getCompany(token.getName());
        verify(employeeRepository, times(1)).findByUsername(data.manager_username());
    }
    @Test
    @DisplayName("Create Project Unsuccessfully - Used Data")
    void newProject_unsuccessful_case01() {
        NewProjectDTO data = mock(NewProjectDTO.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(projectRepository.existsByName(data.name())).thenReturn(true);

        assertThrows(UsedDataException.class, () -> projectService.newProject(data, token));

        verify(projectRepository, times(1)).existsByName(data.name());
    }
    @Test
    @DisplayName("Create Project Unsuccessfully - Invalid Deadline")
    void newProject_unsuccessful_case02() {
        var token = mock(JwtAuthenticationToken.class);
        NewProjectDTO data = mock(NewProjectDTO.class);
        Company company = mock(Company.class);

        when(projectRepository.existsByName(data.name())).thenReturn(false);
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(data.deadline()).thenReturn("01/12/2022");

        assertThrows(InvalidDeadlineException.class, () -> projectService.newProject(data, token));

        verify(projectRepository, times(1)).existsByName(data.name());
        verify(companyService, times(1)).getCompany(token.getName());
    }
    @Test
    @DisplayName("Create Project Unsuccessfully - Non existent Manager")
    void newProject_unsuccessful_case03() {
        var token = mock(JwtAuthenticationToken.class);
        NewProjectDTO data = mock(NewProjectDTO.class);
        Company company = mock(Company.class);
        Optional<Employee> optional = Optional.empty();

        when(projectRepository.existsByName(data.name())).thenReturn(false);
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(data.deadline()).thenReturn("01/12/2030");
        when(employeeRepository.findByUsername(any())).thenReturn(optional);

        assertThrows(NullEmployeeException.class, () -> projectService.newProject(data, token));

        verify(projectRepository, times(1)).existsByName(data.name());
        verify(companyService, times(1)).getCompany(token.getName());
        verify(employeeRepository, times(1)).findByUsername(data.manager_username());
    }
    @Test
    @DisplayName("Create Project Unsuccessfully - Manager Invalid")
    void newProject_unsuccessful_case04() {
        var token = mock(JwtAuthenticationToken.class);
        NewProjectDTO data = mock(NewProjectDTO.class);
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        Employee manager = new Employee("Test Name", "Test Username", "Test CPF",
                "Test Email", "Test Password", company);
        company.setEmployees(new HashSet<>(){{add(manager);}});
        Optional<Employee> optional = Optional.of(manager);
        manager.setProject(mock(Project.class));

        when(projectRepository.existsByName(data.name())).thenReturn(false);
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(data.deadline()).thenReturn("01/12/2030");
        when(employeeRepository.findByUsername(any())).thenReturn(optional);

        assertThrows(InvalidEmployeeException.class, () -> projectService.newProject(data, token));

        verify(projectRepository, times(1)).existsByName(data.name());
        verify(companyService, times(1)).getCompany(token.getName());
        verify(employeeRepository, times(1)).findByUsername(data.manager_username());
    }

    @Test
    @DisplayName("Update Project Successfully")
    void updateProject_successful() {
        Long id = 1L;
        var data = mock(UpdateProjectDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Set<Project> usedData = new HashSet<>();
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        Project project = mock(Project.class);
        company.setProjects(new HashSet<>(){{add(project);}});
        Optional<Project> optionalProject = Optional.of(project);
        Employee manager = mock(Employee.class);
        Optional<Employee> optionalManager = Optional.of(manager);
        company.setEmployees(new HashSet<>(){{add(manager);}});

        when(projectRepository.findByName(data.name())).thenReturn(usedData);
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(projectRepository.findById(id)).thenReturn(optionalProject);
        when(employeeRepository.findByUsername(data.manager_username())).thenReturn(optionalManager);
        when(manager.getRole()).thenReturn(Role.EMPLOYEE);
        when(data.deadline()).thenReturn("01/12/2030");

        assertDoesNotThrow(() -> projectService.updateProject(id, data, token));

        verify(projectRepository, times(1)).findByName(data.name());
        verify(companyService, times(1)).getCompany(token.getName());
        verify(projectRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).findByUsername(data.manager_username());
        verify(projectRepository, times(1)).save(any());
    }
    @Test
    @DisplayName("Update Project Unsuccessfully - Used Data")
    void updateProject_unsuccessful_case01() {
        Long id = 1L;
        UpdateProjectDTO data = new UpdateProjectDTO(
                "Test Name", "Test Description",
                "Test Username", "01/12/2030");
        var token = mock(JwtAuthenticationToken.class);
        Project projectOne = mock(Project.class);
        projectOne.setName("Test Name");
        Set<Project> usedData = new HashSet<>() {{
            add(projectOne);
        }};

        when(projectRepository.findByName(data.name())).thenReturn(usedData);

        assertThrows(UsedDataException.class, () -> projectService.updateProject(id, data, token));

        verify(projectRepository, times(1)).findByName(data.name());
    }
    @Test
    @DisplayName("Update Project Unsuccessfully - Non existent Project")
    void updateProject_unsuccessful_case02() {
        Long id = 1L;
        var data = mock(UpdateProjectDTO.class);
        var token = mock(JwtAuthenticationToken.class);

        when(projectRepository.findByName(data.name())).thenReturn(new HashSet<>());
        when(companyService.getCompany(token.getName())).thenReturn(mock(Company.class));
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullProjectException.class, () -> projectService.updateProject(id, data, token));

        verify(projectRepository, times(1)).findByName(data.name());
        verify(companyService, times(1)).getCompany(token.getName());
        verify(projectRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Update Project Unsuccessfully - Non existent Manager")
    void updateProject_unsuccessful_case03() {
        Long id = 1L;
        var data = mock(UpdateProjectDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Project project = mock(Project.class);
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        company.setProjects(new HashSet<>(){{
            add(project);
        }});

        when(projectRepository.findByName(data.name())).thenReturn(new HashSet<>());
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(employeeRepository.findByUsername(data.manager_username())).thenReturn(Optional.empty());

        assertThrows(NullEmployeeException.class, () -> projectService.updateProject(id, data, token));

        verify(projectRepository, times(1)).findByName(data.name());
        verify(companyService, times(1)).getCompany(token.getName());
        verify(projectRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).findByUsername(data.manager_username());
    }
    @Test
    @DisplayName("Update Project Unsuccessfully - Invalid Deadline")
    void updateProject_unsuccessful_case04() {
        Long id = 1L;
        var data = mock(UpdateProjectDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Project project = mock(Project.class);
        Employee manager = mock(Employee.class);
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        company.setProjects(new HashSet<>(){{add(project);}});
        company.setEmployees(new HashSet<>(){{add(manager);}});

        when(projectRepository.findByName(data.name())).thenReturn(new HashSet<>());
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(employeeRepository.findByUsername(data.manager_username())).thenReturn(Optional.of(manager));
        when(data.deadline()).thenReturn("01/12/2020");
        when(manager.getRole()).thenReturn(Role.EMPLOYEE);

        assertThrows(InvalidDeadlineException.class, () -> projectService.updateProject(id, data, token));

        verify(projectRepository, times(1)).findByName(data.name());
        verify(companyService, times(1)).getCompany(token.getName());
        verify(projectRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).findByUsername(data.manager_username());
        verify(data, times(1)).deadline();
    }

    @Test
    @DisplayName("Delete Project Successfully")
    void deleteProject_successful() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Project project = mock(Project.class);
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        company.setProjects(new HashSet<>(){{add(project);}});

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(companyService.getCompany(token.getName())).thenReturn(company);

        assertDoesNotThrow(() -> projectService.deleteProject(id, token));

        verify(projectRepository, times(1)).deleteById(id);
    }
    @Test
    @DisplayName("Delete Project Unsuccessfully - Non existent Project")
    void deleteProject_unsuccessful_case01() {
        Long id = 1L;
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullProjectException.class, () -> projectService.deleteProject(id, token));

        verify(projectRepository, times(1)).findById(id);
        verify(companyService, never()).getCompany(token.getName());
    }
    @Test
    @DisplayName("Delete Project Unsuccessfully - Isn't its Project")
    void deleteProject_unsuccessful_case02() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Project project = mock(Project.class);
        Company company = mock(Company.class);

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(company.getProjects()).thenReturn(new HashSet<>());

        assertThrows(NullProjectException.class, () -> projectService.deleteProject(id, token));

        verify(projectRepository, times(1)).findById(id);
        verify(companyService, times(1)).getCompany(token.getName());
        verify(projectRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Get Project List Successfully")
    void getAllProjects() {
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        company.setProjects(new HashSet<>(){{
            add(project);
        }});

        when(project.getManager()).thenReturn(manager);
        when(manager.getUsername()).thenReturn("Test Username");
        when(project.getDeadline()).thenReturn(LocalDate.now().plusDays(1L));
        when(companyService.getCompany(token.getName())).thenReturn(company);

        Set<ProjectDTO> projectDTOs = assertDoesNotThrow(() -> projectService.getAllProjects(token));

        assertEquals(1, projectDTOs.size());
        verify(companyService, times(1)).getCompany(token.getName());
    }

    @Test
    @DisplayName("Get Project Successfully")
    void getProject_successful() {
        Long id = 1L;
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        company.setProjects(new HashSet<>(){{
            add(project);
        }});

        when(project.getManager()).thenReturn(manager);
        when(manager.getUsername()).thenReturn("Test Username");
        when(project.getDeadline()).thenReturn(LocalDate.now().plusDays(1L));
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(companyService.getCompany(token.getName())).thenReturn(company);

        ProjectDTO projectDTO = assertDoesNotThrow(() -> projectService.getProject(id, token));

        assertNotNull(projectDTO);
        verify(companyService, times(1)).getCompany(token.getName());
        verify(projectRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Get Project Unsuccessfully - Non existent Project")
    void getProject_unsuccessful_case01() {
        Long id = 1L;
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullProjectException.class, () -> projectService.getProject(id, token));

        verify(projectRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Get Project Unsuccessfully - Isn't its Project")
    void getProject_unsuccessful_case02() {
        Long id = 1L;
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Company company = mock(Company.class);
        Project project = mock(Project.class);

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(company.getProjects()).thenReturn(new HashSet<>());

        assertThrows(NullProjectException.class, () -> projectService.getProject(id, token));

        verify(companyService, times(1)).getCompany(token.getName());
        verify(projectRepository, times(1)).findById(id);
    }
}