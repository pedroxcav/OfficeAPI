package com.office.api.service;

import com.office.api.exception.InvalidDeadlineException;
import com.office.api.exception.NullProjectException;
import com.office.api.exception.NullTaskException;
import com.office.api.exception.UsedDataException;
import com.office.api.model.Employee;
import com.office.api.model.Project;
import com.office.api.model.Task;
import com.office.api.model.dto.task.NewTaskDTO;
import com.office.api.model.dto.task.TaskDTO;
import com.office.api.model.dto.task.UpdateTaskDTO;
import com.office.api.model.enums.Role;
import com.office.api.repository.TaskRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;

    public TaskService(TaskRepository taskRepository, EmployeeService employeeService) {
        this.taskRepository = taskRepository;
        this.employeeService = employeeService;
    }

    public void newTask(NewTaskDTO data, JwtAuthenticationToken token) {
        Employee manager = employeeService.getEmployee(token.getName());

        if(taskRepository.existsByTitle(data.title())) throw new UsedDataException();

        Project project = manager.getProject();

        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime deadline = LocalDateTime.parse(data.deadline(), formatter);
        if(deadline.isBefore(LocalDateTime.now())) throw new InvalidDeadlineException();

        Task task = new Task(
                data.title(),
                data.description(),
                deadline, project);
        taskRepository.save(task);
    }
    public void updateTask(Long id, UpdateTaskDTO data, JwtAuthenticationToken token) {
        Employee manager = employeeService.getEmployee(token.getName());

        Set<Task> usedData = taskRepository.findByTitle(data.title());
        if(usedData.stream().anyMatch(project -> !project.getId().equals(id)))
            throw new UsedDataException();

        Task task = taskRepository.findById(id).orElseThrow(NullTaskException::new);

        if(!manager.getProject().getTasks().contains(task))
            throw new NullTaskException();

        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime deadline = LocalDateTime.parse(data.deadline(), formatter);
        if(deadline.isBefore(LocalDateTime.now())) throw new InvalidDeadlineException();

        task.setTitle(data.title());
        task.setDescription(data.description());
        task.setDeadline(deadline);

        taskRepository.save(task);
    }
    public void deleteTask(Long id, JwtAuthenticationToken token) {
        Employee manager = employeeService.getEmployee(token.getName());

        Task task = taskRepository.findById(id).orElseThrow(NullTaskException::new);

        if(!manager.getProject().getTasks().contains(task))
            throw new NullTaskException();

        taskRepository.delete(task);
    }
    public Set<TaskDTO> getTasks(JwtAuthenticationToken token) {
        Project project;
        Set<Task> tasks;
        Employee employee = employeeService.getEmployee(token.getName());

        try {
            if (employee.getRole().equals(Role.MANAGER))
                project = employee.getProject();
            else
                project = employee.getTeam().getProject();

        tasks = project.getTasks();
        } catch(NullPointerException e) {
            throw new NullProjectException("You aren't working on a Project");
        }

        return TaskDTO.toDTOList(tasks);
    }
    public TaskDTO getTask(Long id, JwtAuthenticationToken token) {
        Employee manager = employeeService.getEmployee(token.getName());

        Task task = taskRepository.findById(id).orElseThrow(NullTaskException::new);

        if(!manager.getProject().getTasks().contains(task))
            throw new NullTaskException();

        return TaskDTO.toDTO(task);
    }
}
