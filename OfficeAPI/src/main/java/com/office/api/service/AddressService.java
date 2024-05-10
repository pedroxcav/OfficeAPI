package com.office.api.service;

import com.office.api.exception.NullCompanyException;
import com.office.api.model.Address;
import com.office.api.model.Company;
import com.office.api.model.dto.address.AddressDTO;
import com.office.api.model.dto.address.UpdateAddressDTO;
import com.office.api.repository.AddressRepository;
import com.office.api.repository.CompanyRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CompanyRepository companyRepository;

    public AddressService(AddressRepository addressRepository, CompanyRepository companyRepository) {
        this.addressRepository = addressRepository;
        this.companyRepository = companyRepository;
    }

    public void updateAddress(UpdateAddressDTO data, JwtAuthenticationToken token) {
        UUID companyId = UUID.fromString(token.getName());
        Company company = companyRepository.findById(companyId)
                .orElseThrow(NullCompanyException::new);

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
        UUID companyId = UUID.fromString(token.getName());
        Company company = companyRepository.findById(companyId)
                .orElseThrow(NullCompanyException::new);

        Address address = company.getAddress();
        return AddressDTO.toDTO(address);
    }
}
