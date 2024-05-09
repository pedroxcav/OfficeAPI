package com.office.api.model;

import com.office.api.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "company", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Address address;
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private Set<Employee> employees;
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private Set<Project> projects;
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private Set<Team> teams;

    public Company(String name, String cnpj, String password) {
        this.name = name;
        this.cnpj = cnpj;
        this.password = password;
        this.role = Role.COMPANY;
    }
}