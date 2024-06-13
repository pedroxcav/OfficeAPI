package com.office.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;
    @CreationTimestamp
    @Column(name = "posted_at", nullable = false)
    private LocalDateTime postedAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Employee owner;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    public Comment(String content, Employee owner, Task task) {
        this.content = content;
        this.owner = owner;
        this.task = task;
    }
}