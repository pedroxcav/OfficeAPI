package com.office.api.model;

import com.office.api.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "companies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
    @CNPJ
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "company")
    private Address address;
    @OneToMany(mappedBy = "company")
    private Set<Employee> employees;
    @OneToMany(mappedBy = "company")
    private Set<Project> projects;
    @OneToMany(mappedBy = "company")
    private Set<Team> teams;
}