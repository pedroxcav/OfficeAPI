package com.office.api.repository;

import com.office.api.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByUsername(String username);
    boolean existsByUsernameOrCpfOrEmail(String username, String cpf, String email);
    Set<Employee> findAllByUsernameOrEmail(String username, String email);
}
