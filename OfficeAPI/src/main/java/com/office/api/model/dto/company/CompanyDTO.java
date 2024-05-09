package com.office.api.model.dto.company;

import com.office.api.model.Company;
import com.office.api.model.dto.address.AddressDTO;

public record CompanyDTO(String name,
                         String cnpj,
                         AddressDTO address) {

    public static CompanyDTO toDTO(Company company) {
        return new CompanyDTO(
                company.getName(),
                company.getCnpj(),
                AddressDTO.toDTO(company.getAddress()));
    }
}
