package com.office.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDate deadline;
    @Column(nullable = false)
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    @OneToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;
    @OneToMany(mappedBy = "project")
    private Set<Task> tasks;
    @OneToMany(mappedBy = "project")
    private Set<Team> teams;
}