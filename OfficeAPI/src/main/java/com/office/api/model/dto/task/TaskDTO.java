package com.office.api.model.dto.task;

import com.office.api.model.Task;
import com.office.api.model.dto.comment.CommentDTO;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

public record TaskDTO(
        Long id,
        String title,
        String description,
        String deadline,
        boolean status,
        Set<CommentDTO> comments) {

    public static TaskDTO toDTO(Task task) {
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                formatter.format(task.getDeadline()),
                task.isActive(),
                CommentDTO.toDTOList(task.getComments()));
    }
    public static Set<TaskDTO> toDTOList(Set<Task> tasks) {
        return tasks.stream().map(TaskDTO::toDTO).collect(Collectors.toSet());
    }
}
