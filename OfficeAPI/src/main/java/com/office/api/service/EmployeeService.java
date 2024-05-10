package com.office.api.service;

import com.office.api.exception.LoginFailedException;
import com.office.api.exception.NullEmployeeException;
import com.office.api.exception.UsedDataException;
import com.office.api.model.Company;
import com.office.api.model.Employee;
import com.office.api.model.dto.employee.*;
import com.office.api.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class EmployeeService {
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder encoder;
    private final CompanyService companyService;
    private final EmployeeRepository employeeRepository;

    public EmployeeService(JwtEncoder jwtEncoder, PasswordEncoder encoder, CompanyService companyService, EmployeeRepository employeeRepository) {
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
        this.companyService = companyService;
        this.employeeRepository = employeeRepository;
    }
    public LoginResponseDTO login(LoginRequestDTO data) {
        Employee employee = employeeRepository.findByUsername(data.username())
                .orElseThrow(() -> new LoginFailedException("Employee not found"));
        if(!encoder.matches(data.password(), employee.getPassword()))
            throw new LoginFailedException("Password does not match");

        var expiresIn = 86400L;
        var now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("office.api")
                .subject(employee.getId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", employee.getRole())
                .issuedAt(now)
                .build();

        Jwt accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims));
        return new LoginResponseDTO(accessToken.getTokenValue(), expiresIn);
    }
    public void newEmployee(NewEmployeeDTO data, JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());

        if(employeeRepository.existsByUsernameOrCpfOrEmail(data.username(), data.cpf(), data.email()))
            throw new UsedDataException();

        Employee employee = new Employee(
                data.name(),
                data.username(),
                data.cpf(),
                data.email(),
                encoder.encode(data.password()),
                company);
        employeeRepository.save(employee);
    }
    public void updateEmployee(UpdateEmployeeDTO data, JwtAuthenticationToken token) {
        UUID employeeId = UUID.fromString(token.getName());
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(NullEmployeeException::new);

        Set<Employee> usedData = employeeRepository.findAllByUsernameOrEmail(data.username(), data.email());
        if(usedData.stream().anyMatch(employeeValue -> !employeeValue.getId().equals(employee.getId())))
            throw new UsedDataException();

        employee.setName(data.name());
        employee.setUsername(data.username());
        employee.setEmail(data.email());
        employee.setPassword(encoder.encode(data.password()));

        employeeRepository.save(employee);
    }
    public void removeEmployee(String username, JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(NullEmployeeException::new);

        if(!company.getEmployees().contains(employee))
            throw new NullEmployeeException();

        employeeRepository.delete(employee);
    }
    public Set<EmployeeDTO> getAllEmployees(JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());

        Set<Employee> employees = company.getEmployees();
        return EmployeeDTO.toDTOList(employees);
    }
    public EmployeeDTO getEmployee(String username, JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());
        Optional<Employee> optionalEmployee = employeeRepository.findByUsername(username);

        if(optionalEmployee.isEmpty() || !company.getEmployees().contains(optionalEmployee.get()))
            throw new NullEmployeeException();

        return EmployeeDTO.toDTO(optionalEmployee.get());
    }
    public EmployeeDTO getEmployee(JwtAuthenticationToken token) {
        UUID employeeId = UUID.fromString(token.getName());
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(NullEmployeeException::new);

        return EmployeeDTO.toDTO(employee);
    }
    public Employee getEmployee(String employeeId) {
        UUID id = UUID.fromString(employeeId);
        return employeeRepository.findById(id)
                .orElseThrow(NullEmployeeException::new);
    }
}
