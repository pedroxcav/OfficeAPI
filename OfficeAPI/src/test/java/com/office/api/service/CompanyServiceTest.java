package com.office.api.service;

import com.office.api.exception.LoginFailedException;
import com.office.api.exception.NullCompanyException;
import com.office.api.exception.UsedDataException;
import com.office.api.model.Address;
import com.office.api.model.Company;
import com.office.api.model.dto.address.NewAddressDTO;
import com.office.api.model.dto.company.*;
import com.office.api.model.enums.Role;
import com.office.api.repository.CompanyRepository;
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

class CompanyServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private CompanyRepository companyRepository;
    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Login Successfully")
    void login_successful() {
        Jwt accessToken = mock(Jwt.class);
        Company company = mock(Company.class);
        LoginRequestDTO data = mock(LoginRequestDTO.class);

        when(company.getRole()).thenReturn(Role.COMPANY);
        when(company.getId()).thenReturn(UUID.randomUUID());
        when(jwtEncoder.encode(any())).thenReturn(accessToken);
        when(encoder.matches(any(), any())).thenReturn(true);
        when(companyRepository.findByName(any())).thenReturn(Optional.of(company));

        LoginResponseDTO response = companyService.login(data);

        verify(jwtEncoder, times(1)).encode(any());
        verify(encoder, times(1)).matches(any(), any());
        verify(companyRepository, times(1)).findByName(any());

        assertNotNull(response);
    }
    @Test
    @DisplayName("Login Unsuccessfully - Non existent Company")
    void login_unsuccessful_case01() {
        LoginRequestDTO data = mock(LoginRequestDTO.class);

        when(companyRepository.findByName(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> companyService.login(data));

        verify(companyRepository, times(1)).findByName(any());
    }
    @Test
    @DisplayName("Login Unsuccessfully - Incorrect Password")
    void login_unsuccessful_case02() {
        Company company = mock(Company.class);
        LoginRequestDTO data = mock(LoginRequestDTO.class);

        when(encoder.matches(any(), any())).thenReturn(false);
        when(companyRepository.findByName(any())).thenReturn(Optional.of(company));

        assertThrows(LoginFailedException.class, () -> companyService.login(data));

        verify(encoder, times(1)).matches(any(), any());
        verify(companyRepository, times(1)).findByName(any());
    }

    @Test
    @DisplayName("Register Company Successfully")
    void newCompany_successful() {
        NewAddressDTO addressDTO = mock(NewAddressDTO.class);
        NewCompanyDTO data = mock(NewCompanyDTO.class);

        when(data.address()).thenReturn(addressDTO);
        when(data.name()).thenReturn("Test Name");
        when(data.cnpj()).thenReturn("Test CNPJ");
        when(encoder.encode(any())).thenReturn("Test Encoded Password");
        when(companyRepository.existsByNameOrCnpj(any(), any())).thenReturn(false);

        assertDoesNotThrow(() -> companyService.newCompany(data));

        verify(data, times(2)).name();
        verify(data, times(2)).cnpj();
        verify(encoder, times(1)).encode(any());
        verify(companyRepository, times(1)).save(any());
        verify(companyRepository, times(1)).existsByNameOrCnpj(any(), any());
    }
    @Test
    @DisplayName("Register Company Unsuccessfully")
    void newCompany_unsuccessful() {
        NewCompanyDTO data = mock(NewCompanyDTO.class);

        when(companyRepository.existsByNameOrCnpj(any(), any())).thenReturn(true);

        assertThrows(UsedDataException.class, () -> companyService.newCompany(data));

        verify(companyRepository, times(1)).existsByNameOrCnpj(any(), any());
    }

    @Test
    @DisplayName("Update Company Successfully")
    void updateCompany_successful() {
        Company company = mock(Company.class);
        Set<Company> usedData = new HashSet<>();
        var token = mock(JwtAuthenticationToken.class);
        String encodedPassword = "Test Encoded Password";
        UpdateCompanyDTO data = mock(UpdateCompanyDTO.class);

        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(encoder.encode(data.password())).thenReturn(encodedPassword);
        when(companyRepository.findById(any())).thenReturn(Optional.of(company));
        when(companyRepository.findAllByNameOrCnpj(any(), any())).thenReturn(usedData);

        assertDoesNotThrow(() -> companyService.updateCompany(data, token));

        verify(company, times(1)).setName(data.name());
        verify(company, times(1)).setCnpj(data.cnpj());
        verify(company, times(1)).setPassword(encodedPassword);
        verify(companyRepository, times(1)).findAllByNameOrCnpj(any(), any());
    }
    @Test
    @DisplayName("Update Company Unsuccessfully")
    void updateCompany_Unsuccessful() {
        Company company01 = new Company("Test Name", "Test CNPJ", "Test Password");
        Company company02 = new Company("Test Name", "Test CNPJ", "Test Password");
        Set<Company> usedData = new HashSet<>(){{add(company01);}};
        UpdateCompanyDTO data = mock(UpdateCompanyDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        company01.setId(UUID.randomUUID());
        company02.setId(UUID.randomUUID());

        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(companyRepository.findById(any())).thenReturn(Optional.of(company02));
        when(companyRepository.findAllByNameOrCnpj(any(), any())).thenReturn(usedData);

        assertThrows(UsedDataException.class, () -> companyService.updateCompany(data, token));

        verify(companyRepository, times(1)).findAllByNameOrCnpj(any(), any());
    }

    @Test
    @DisplayName("Remove Company Successfully")
    void removeCompany_successful() {
        Company company = mock(Company.class);
        var token = mock(JwtAuthenticationToken.class);

        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(companyRepository.findById(any())).thenReturn(Optional.of(company));

        assertDoesNotThrow(() -> companyService.removeCompany(token));

        verify(companyRepository, times(1)).delete(any());
        verify(companyRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Get Company by the Token")
    void getCompany_successful_case01() {
        var token = mock(JwtAuthenticationToken.class);
        Company company = new Company("Test Name", "Test CNPJ", "Test Password");
        company.setAddress(new Address(new NewAddressDTO("08500000", "123", "Test Street",
                "Test Neighborhood", "Test City", "Test State"), company));

        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(companyRepository.findById(any())).thenReturn(Optional.of(company));

        CompanyDTO data = assertDoesNotThrow(() -> companyService.getCompany(token));
        assertEquals(data.name(), company.getName());
        assertEquals(data.cnpj(), company.getCnpj());
        assertNotNull(data);

        verify(companyRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Get Company by Id Successfully")
    void getCompany_successful_case02() {
        Company company = mock(Company.class);
        var token = mock(JwtAuthenticationToken.class);

        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(companyRepository.findById(any())).thenReturn(Optional.of(company));

        assertDoesNotThrow(() -> companyService.getCompany(token.getName()));

        verify(companyRepository, times(1)).findById(any());
    }
    @Test
    @DisplayName("Get Company by Id Unsuccessfully")
    void getCompany_unsuccessful() {
        var token = mock(JwtAuthenticationToken.class);

        when(token.getName()).thenReturn(UUID.randomUUID().toString());
        when(companyRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NullCompanyException.class, () -> companyService.getCompany(token.getName()));

        verify(companyRepository, times(1)).findById(any());
    }
}