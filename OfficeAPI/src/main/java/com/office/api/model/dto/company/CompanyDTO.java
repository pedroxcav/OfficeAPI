package com.office.api.model.dto.company;

import com.office.api.model.Company;
import com.office.api.model.dto.address.AddressDTO;
import com.office.api.model.dto.employee.EmployeeDTO;
import com.office.api.model.dto.project.ProjectDTO;
import com.office.api.model.dto.team.TeamDTO;

import java.util.Set;

public record CompanyDTO(String name,
                         String cnpj,
                         AddressDTO address,
                         Set<EmployeeDTO> employees,
                         Set<ProjectDTO> projects,
                         Set<TeamDTO> teams) {

    public static CompanyDTO toDTO(Company company) {
        return new CompanyDTO(
                company.getName(),
                company.getCnpj(),
                AddressDTO.toDTO(company.getAddress()),
                EmployeeDTO.toDTOList(company.getEmployees()),
                ProjectDTO.toDTOList(company.getProjects()),
                TeamDTO.toDTOList(company.getTeams()));
    }
}
