package com.office.api.repository;

import com.office.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByName(String name);

    boolean existsByNameOrCnpj(String name, String cnpj);

    Set<Company> findAllByNameOrCnpj(String name, String cnpj);
}
