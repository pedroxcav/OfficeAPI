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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectService {
    private final CompanyService companyService;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectService(ProjectRepository projectRepository, EmployeeRepository employeeRepository, CompanyService companyService) {
        this.companyService = companyService;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    public void newProject(NewProjectDTO data, JwtAuthenticationToken token) {
        if(projectRepository.existsByName(data.name())) throw new UsedDataException();
        Company company = companyService.getCompany(token.getName());

        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate deadline = LocalDate.parse(data.deadline(), formatter);
        if (deadline.isBefore(LocalDate.now())) throw new InvalidDeadlineException();

        Optional<Employee> optionalManager = employeeRepository.findByUsername(data.manager_username());
        if(optionalManager.isEmpty() || !company.getEmployees().contains(optionalManager.get()))
            throw new NullEmployeeException();
        else if (optionalManager.get().getProject() != null)
            throw new InvalidEmployeeException("Already manage a project");

        Employee manager = optionalManager.get();
        if(!manager.getRole().equals(Role.MANAGER)) {
            manager.setRole(Role.MANAGER);
            manager.setTeam(null);
        }

        Project project = new Project(
                data.name(),
                data.description(),
                deadline, company, manager);
        projectRepository.save(project);
    }
    public void updateProject(Long id, UpdateProjectDTO data, JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());

        Optional<Project> optionalProject = projectRepository.findById(id);
        if(optionalProject.isEmpty() || !company.getProjects().contains(optionalProject.get()))
            throw new NullProjectException();

        Optional<Employee> optionalManager = employeeRepository.findByUsername(data.manager_username());
        if(optionalManager.isEmpty() || !company.getEmployees().contains(optionalManager.get()))
            throw new NullEmployeeException();

        Employee manager = optionalManager.get();
        if(!manager.getRole().equals(Role.MANAGER))
            manager.setRole(Role.MANAGER);

        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate deadline = LocalDate.parse(data.deadline(), formatter);
        if(deadline.isBefore(LocalDate.now())) throw new InvalidDeadlineException();

        var project = optionalProject.get();
        project.setName(data.name());
        project.setDescription(data.description());
        project.setDeadline(deadline);
        project.setManager(manager);

        projectRepository.save(project);
    }
    public void deleteProject(Long projectId, JwtAuthenticationToken token) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NullProjectException::new);

        Company company = companyService.getCompany(token.getName());

        if(!company.getProjects().contains(project))
            throw new NullProjectException();

        project.getTeams().forEach(team -> team.setProject(null));
        projectRepository.deleteById(projectId);
    }
    public Set<ProjectDTO> getAllProjects(JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());
        Set<Project> projects = company.getProjects();
        return ProjectDTO.toDTOList(projects);
    }
    public ProjectDTO getProject(Long projectId, JwtAuthenticationToken token) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NullProjectException::new);

        Company company = companyService.getCompany(token.getName());

        if(!company.getProjects().contains(project))
            throw new NullProjectException();

        return ProjectDTO.toDTO(project);
    }
}
