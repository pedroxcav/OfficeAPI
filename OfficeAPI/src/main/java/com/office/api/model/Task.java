package com.office.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDateTime deadline;
    @Column(nullable = false)
    private boolean expired;

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private Set<Comment> comments;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public Task(String title, String description, LocalDateTime deadline, Project project) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.expired = false;
        this.project = project;
    }
}