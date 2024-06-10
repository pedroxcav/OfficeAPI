package com.office.api.service;

import com.office.api.exception.LoginFailedException;
import com.office.api.exception.NullEmployeeException;
import com.office.api.exception.UsedDataException;
import com.office.api.model.Company;
import com.office.api.model.Employee;
import com.office.api.model.dto.employee.*;
import com.office.api.model.enums.Role;
import com.office.api.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private CompanyService companyService;
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Login Successfully")
    void login_successful() {
        Employee employee = mock(Employee.class);
        LoginRequestDTO data = mock(LoginRequestDTO.class);

        when(employeeRepository.findByUsername(data.username())).thenReturn(Optional.of(employee));
        when(encoder.matches(data.password(), employee.getPassword())).thenReturn(true);
        when(employee.getId()).thenReturn(UUID.randomUUID());
        when(employee.getRole()).thenReturn(Role.EMPLOYEE);
        when(jwtEncoder.encode(any())).thenReturn(mock(Jwt.class));

        LoginResponseDTO response = assertDoesNotThrow(() -> employeeService.login(data));

        assertNotNull(response);
        verify(employeeRepository, times(1)).findByUsername(data.username());
    }
    @Test
    @DisplayName("Login Unsuccessfully - Non existent Employee")
    void login_unsuccessful_case01() {
        LoginRequestDTO data = mock(LoginRequestDTO.class);

        when(employeeRepository.findByUsername(data.username())).thenReturn(Optional.empty());

        assertThrows(LoginFailedException.class, () -> employeeService.login(data));

        verify(employeeRepository, times(1)).findByUsername(data.username());
    }
    @Test
    @DisplayName("Login Unsuccessfully - Invalid Password")
    void login_unsuccessful_case02() {
        Employee employee = mock(Employee.class);
        LoginRequestDTO data = mock(LoginRequestDTO.class);

        when(employeeRepository.findByUsername(data.username())).thenReturn(Optional.of(employee));
        when(encoder.matches(data.password(), employee.getPassword())).thenReturn(false);

        assertThrows(LoginFailedException.class, () -> employeeService.login(data));

        verify(employeeRepository, times(1)).findByUsername(data.username());
    }

    @Test
    @DisplayName("Registers Employee Successfully")
    void newEmployee_successful() {
        NewEmployeeDTO data = mock(NewEmployeeDTO.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Company company = mock(Company.class);

        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(employeeRepository.existsByUsernameOrCpfOrEmail(data.username(), data.cpf(), data.email()))
                .thenReturn(false);

        assertDoesNotThrow(() -> employeeService.newEmployee(data, token));

        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(employeeRepository, times(1))
                .existsByUsernameOrCpfOrEmail(data.username(), data.cpf(), data.email());
        verify(companyService, times(1)).getCompany(token.getName());
    }
    @Test
    @DisplayName("Registers Employee Unsuccessfully")
    void newEmployee_unsuccessful() {
        NewEmployeeDTO data = mock(NewEmployeeDTO.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Company company = mock(Company.class);

        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(employeeRepository.existsByUsernameOrCpfOrEmail(data.username(), data.cpf(), data.email()))
                .thenReturn(true);

        assertThrows(UsedDataException.class, () -> employeeService.newEmployee(data, token));

        verify(employeeRepository, never()).save(any(Employee.class));
        verify(employeeRepository, times(1))
                .existsByUsernameOrCpfOrEmail(data.username(), data.cpf(), data.email());
        verify(companyService, times(1)).getCompany(token.getName());
    }

    @Test
    @DisplayName("Update Employee Successfully")
    void updateEmployee_successful() {
        UpdateEmployeeDTO data = mock(UpdateEmployeeDTO.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Employee employee = mock(Employee.class);

        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(employeeRepository.findById(any())).thenReturn(Optional.of(employee));
        when(employeeRepository.findAllByUsernameOrEmail(data.username(), data.email()))
                .thenReturn(new HashSet<>());

        assertDoesNotThrow(() -> employeeService.updateEmployee(data, token));

        verify(employeeRepository, times(1)).findById(any(UUID.class));
        verify(employeeRepository, times(1)).findAllByUsernameOrEmail(data.username(), data.email());
        verify(employeeRepository, times(1)).save(employee);
    }
    @Test
    @DisplayName("Update Employee Unsuccessfully - Non existent Employee")
    void updateEmployee_unsuccessful_case01() {
        UpdateEmployeeDTO data = mock(UpdateEmployeeDTO.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(employeeRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NullEmployeeException.class, () -> employeeService.updateEmployee(data, token));

        verify(employeeRepository, times(1)).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    @Test
    @DisplayName("Update Employee Unsuccessfully - Used Data")
    void updateEmployee_unsuccessful_case02() {
        UpdateEmployeeDTO data = mock(UpdateEmployeeDTO.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Employee employeeOne = mock(Employee.class);
        Employee employeeTwo = mock(Employee.class);
        Set<Employee> usedData = new HashSet<>() {{
            add(employeeOne);
        }};

        when(employeeOne.getId()).thenReturn(UUID.randomUUID());
        when(employeeTwo.getId()).thenReturn(UUID.randomUUID());
        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(employeeRepository.findById(any())).thenReturn(Optional.of(employeeTwo));
        when(employeeRepository.findAllByUsernameOrEmail(data.username(), data.email()))
                .thenReturn(usedData);

        assertThrows(UsedDataException.class, () -> employeeService.updateEmployee(data, token));

        verify(employeeRepository, times(1)).findById(any(UUID.class));
        verify(employeeRepository, times(1)).findAllByUsernameOrEmail(data.username(), data.email());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Remove Employee Successfully")
    void removeEmployee_successful() {
        String username = "Test Username";
        var token = mock(JwtAuthenticationToken.class);
        Company company = mock(Company.class);
        Employee employee = mock(Employee.class);
        Set<Employee> employees = new HashSet<>() {{add(employee);}};

        when(company.getEmployees()).thenReturn(employees);
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(employee));

        assertDoesNotThrow(() -> employeeService.removeEmployee(username, token));

        verify(companyService, times(1)).getCompany(token.getName());
        verify(employeeRepository, times(1)).findByUsername(username);
        verify(employeeRepository, times(1)).delete(employee);

    }
    @Test
    @DisplayName("Remove Employee Unsuccessfully - Non existent Employee")
    void removeEmployee_unsuccessful_case01() {
        String username = "Test Username";
        var token = mock(JwtAuthenticationToken.class);

        when(employeeRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NullEmployeeException.class, () -> employeeService.removeEmployee(username, token));

        verify(companyService, times(1)).getCompany(token.getName());
        verify(employeeRepository, times(1)).findByUsername(username);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
    @Test
    @DisplayName("Remove Employee Unsuccessfully - Isn't its Employee")
    void removeEmployee_unsuccessful_case02() {
        String username = "Test Username";
        var token = mock(JwtAuthenticationToken.class);
        Company company = mock(Company.class);
        Employee employee = mock(Employee.class);
        Set<Employee> employees = new HashSet<>();

        when(company.getEmployees()).thenReturn(employees);
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(employee));

        assertThrows(NullEmployeeException.class, () -> employeeService.removeEmployee(username, token));

        verify(companyService, times(1)).getCompany(token.getName());
        verify(employeeRepository, times(1)).findByUsername(username);
        verify(employeeRepository, never()).delete(employee);
    }

    @Test
    @DisplayName("Get All Employees Successfully")
    void getAllEmployees() {
        Company company = mock(Company.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Employee employee = mock(Employee.class);
        Set<Employee> employees = new HashSet<>(){{add(employee);}};


        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(company.getEmployees()).thenReturn(employees);
        when(employee.getCompany()).thenReturn(company);
        when(company.getName()).thenReturn("Test Name");

        Set<EmployeeDTO> employeeDTOs = assertDoesNotThrow(() -> employeeService.getAllEmployees(token));

        assertEquals(1, employeeDTOs.size());
        verify(companyService, times(1)).getCompany(token.getName());
    }

    @Test
    @DisplayName("Company Gets Employee Successfully")
    void getEmployee_company_successful() {
        String username = "Test Username";
        var token = mock(JwtAuthenticationToken.class);
        Company company = mock(Company.class);
        Employee employee = mock(Employee.class);
        Set<Employee> employees = new HashSet<>(){{add(employee);}};

        when(employee.getCompany()).thenReturn(company);
        when(company.getName()).thenReturn("Test Name");
        when(company.getEmployees()).thenReturn(employees);
        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(employee));

        EmployeeDTO employeeDTO = assertDoesNotThrow(() -> employeeService.getEmployee(username, token));

        assertNotNull(employeeDTO);
        verify(companyService, times(1)).getCompany(token.getName());
        verify(employeeRepository, times(1)).findByUsername(username);
    }
    @Test
    @DisplayName("Company Gets Employee Unsuccessfully")
    void getEmployee_company_unsuccessful() {
        String username = "Test Username";
        var token = mock(JwtAuthenticationToken.class);
        Company company = mock(Company.class);

        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NullEmployeeException.class, () -> employeeService.getEmployee(username, token));

        verify(companyService, times(1)).getCompany(token.getName());
        verify(employeeRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Employee Gets Employee Unsuccessfully")
    void getEmployee_employee_successful() {
        UUID id = UUID.randomUUID();
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Company company = mock(Company.class);
        Employee employee = mock(Employee.class);
        Set<Employee> employees = new HashSet<>(){{add(employee);}};

        when(employee.getCompany()).thenReturn(company);
        when(company.getName()).thenReturn("Test Name");
        when(company.getEmployees()).thenReturn(employees);
        when(token.getName()).thenReturn(id.toString());
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        EmployeeDTO employeeDTO = assertDoesNotThrow(() -> employeeService.getEmployee(token));

        assertNotNull(employeeDTO);
        verify(employeeRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Employee Gets Employee Successfully")
    void getEmployee_employee_unsuccessful() {
        UUID id = UUID.randomUUID();
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

        when(token.getName()).thenReturn(id.toString());
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullEmployeeException.class, () -> employeeService.getEmployee(token));

        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Get Employee Successfully")
    void getEmployee_successful() {
        UUID id = UUID.randomUUID();
        Employee employee = mock(Employee.class);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        assertDoesNotThrow(() -> employeeService.getEmployee(id.toString()));

        verify(employeeRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Get Employee Unsuccessfully")
    void getEmployee_unsuccessful() {
        UUID id = UUID.randomUUID();

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullEmployeeException.class, () -> employeeService.getEmployee(id.toString()));

        verify(employeeRepository, times(1)).findById(id);
    }
}