package com.office.api.model.dto.employee;

import com.office.api.model.Employee;
import com.office.api.model.dto.project.ProjectDTO;
import com.office.api.model.dto.team.TeamDTO;

import java.util.Set;
import java.util.stream.Collectors;

public record EmployeeDTO(String name,
                          String username,
                          String cpf,
                          String email,
                          TeamDTO team,
                          ProjectDTO project,
                          String company_name) {

    public static EmployeeDTO toDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getName(),
                employee.getUsername(),
                employee.getCpf(),
                employee.getEmail(),
                TeamDTO.toDTO(employee.getTeam()),
                ProjectDTO.toDTO(employee.getProject()),
                employee.getCompany().getName());
    }

    public static Set<EmployeeDTO> toDTOList(Set<Employee> employees) {
        return employees.stream().map(EmployeeDTO::toDTO).collect(Collectors.toSet());
    }
}
