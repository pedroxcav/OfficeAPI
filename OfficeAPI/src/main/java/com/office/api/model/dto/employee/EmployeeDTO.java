package com.office.api.model.dto.employee;

import com.office.api.model.Employee;

import java.util.Set;
import java.util.stream.Collectors;

public record EmployeeDTO() {

    public static EmployeeDTO toDTO(Employee employee) {
        return new EmployeeDTO();
    }

    public static Set<EmployeeDTO> toDTOList(Set<Employee> employees) {
        return employees.stream().map(EmployeeDTO::toDTO).collect(Collectors.toSet());
    }
}
