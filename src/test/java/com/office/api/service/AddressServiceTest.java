package com.office.api.service;

import com.office.api.model.Address;
import com.office.api.model.Company;
import com.office.api.model.dto.address.AddressDTO;
import com.office.api.model.dto.address.NewAddressDTO;
import com.office.api.model.dto.address.UpdateAddressDTO;
import com.office.api.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AddressServiceTest {
    @Mock
    private CompanyService companyService;
    @Mock
    private AddressRepository addressRepository;
    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Update Successfully")
    void updateAddress() {
        String companyName = "testCompany";
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        when(token.getName()).thenReturn(companyName);

        UpdateAddressDTO data = mock(UpdateAddressDTO.class);
        when(data.zipCode()).thenReturn("08500000");
        when(data.number()).thenReturn("123");
        when(data.street()).thenReturn("Test Street");
        when(data.neighborhood()).thenReturn("Test Neighborhood");
        when(data.city()).thenReturn("Test City");
        when(data.state()).thenReturn("Test State");

        Address address = new Address();
        Company company = mock(Company.class);
        when(company.getAddress()).thenReturn(address);
        when(companyService.getCompany(token.getName())).thenReturn(company);

        // When
        addressService.updateAddress(data, token);

        // Then
        verify(companyService).getCompany(companyName);
        verify(data).zipCode();
        verify(data).number();
        verify(data).street();
        verify(data).neighborhood();
        verify(data).city();
        verify(data).state();
        verify(addressRepository).save(address);
    }

    @Test
    @DisplayName("Get Address Successfully")
    void getAddress() {
        Company company = mock(Company.class);
        JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
        Address address = new Address(new NewAddressDTO(
                        "08500000", "123", "Test Street",
                        "Test Neighborhood", "Test City",
                        "Test State"), company);

        when(companyService.getCompany(token.getName())).thenReturn(company);
        when(company.getAddress()).thenReturn(address);

        AddressDTO addressDTO = addressService.getAddress(token);

        verify(companyService, times(1)).getCompany(token.getName());
        verify(company, times(1)).getAddress();

        assertEquals(address.getZipCode(),addressDTO.zipCode());
        assertEquals(address.getNumber(), addressDTO.number());
        assertEquals(address.getStreet(), addressDTO.street());
        assertEquals(address.getNeighborhood(), addressDTO.neighborhood());
        assertEquals(address.getCity(), addressDTO.city());
        assertEquals(address.getState(), addressDTO.state());

    }
}