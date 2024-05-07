package com.office.api.service;

import com.office.api.exception.LoginFailedException;
import com.office.api.exception.NullCompanyException;
import com.office.api.exception.UsedDataException;
import com.office.api.model.Address;
import com.office.api.model.Company;
import com.office.api.model.dto.company.*;
import com.office.api.repository.AddressRepository;
import com.office.api.repository.CompanyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class CompanyService {
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder encoder;
    private final CompanyRepository companyRepository;
    private final AddressRepository addressRepository;

    public CompanyService(JwtEncoder jwtEncoder, PasswordEncoder encoder, CompanyRepository companyRepository, AddressRepository addressRepository) {
        this.jwtEncoder = jwtEncoder;
        this.encoder = encoder;
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO data) {
        Company company = companyRepository.findByName(data.name())
                .orElseThrow(() -> new LoginFailedException("Company not found"));
        if(!encoder.matches(data.password(), company.getPassword()))
            throw new LoginFailedException("Password does not match");

        var expiresIn = 86400L;
        var now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("office.api")
                .subject(company.getId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", company.getRole())
                .issuedAt(now)
                .build();

        Jwt accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims));
        return new LoginResponseDTO(accessToken.getTokenValue(), expiresIn);
    }
    public void newCompany(NewCompanyDTO data) {
        if(companyRepository.existsByNameOrCnpj(data.name(), data.cnpj()))
            throw new UsedDataException();

        Company company = companyRepository.save(
                new Company(
                        data.name(),
                        data.cnpj(),
                        encoder.encode(data.password())));
        addressRepository.save(new Address(data.address(), company));
    }
    public void updateCompany(UpdateCompanyDTO data, JwtAuthenticationToken token) {
        UUID companyId = UUID.fromString(token.getName());
        Company company = companyRepository.findById(companyId)
                .orElseThrow(NullCompanyException::new);
        Set<Company> usedData = companyRepository.findAllByNameOrCnpj(data.name(), data.cnpj());
        if(usedData.stream().anyMatch(companyValue -> !companyValue.getId().equals(company.getId())))
            throw new UsedDataException();

        company.setName(data.name());
        company.setCnpj(data.cnpj());
        company.setPassword(encoder.encode(data.password()));

        companyRepository.save(company);
    }
    public void removeCompany(JwtAuthenticationToken token) {
        UUID companyID = UUID.fromString(token.getName());
        Company company = companyRepository.findById(companyID)
                .orElseThrow(NullCompanyException::new);
        companyRepository.delete(company);
    }
    public CompanyDTO getCompany(JwtAuthenticationToken token) {
        System.out.println(token.getName());
        UUID companyId = UUID.fromString(token.getName());
        Company company = companyRepository.findById(companyId)
                .orElseThrow(NullCompanyException::new);
        return CompanyDTO.toDTO(company);
    }
}
