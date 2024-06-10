package com.office.api.service;

import com.office.api.model.Address;
import com.office.api.model.Company;
import com.office.api.model.dto.address.AddressDTO;
import com.office.api.model.dto.address.UpdateAddressDTO;
import com.office.api.repository.AddressRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CompanyService companyService;

    public AddressService(AddressRepository addressRepository, CompanyService companyService) {
        this.addressRepository = addressRepository;
        this.companyService = companyService;
    }

    public void updateAddress(UpdateAddressDTO data, JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());

        Address address = company.getAddress();
        address.setZipCode(data.zipCode());
        address.setNumber(data.number());
        address.setStreet(data.street());
        address.setNeighborhood(data.neighborhood());
        address.setCity(data.city());
        address.setState(data.state());

        addressRepository.save(address);
    }
    public AddressDTO getAddress(JwtAuthenticationToken token) {
        Company company = companyService.getCompany(token.getName());

        Address address = company.getAddress();
        return AddressDTO.toDTO(address);
    }
}
