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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private EmployeeService employeeService;
    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Creates Task Successfully")
    void newTask_successful() {
        NewTaskDTO data = mock(NewTaskDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.existsByTitle(data.title())).thenReturn(false);
        when(manager.getProject()).thenReturn(project);
        when(data.deadline()).thenReturn("01/12/2030 21:00");

        assertDoesNotThrow(() -> taskService.newTask(data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).existsByTitle(data.title());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    @Test
    @DisplayName("Creates Task Unsuccessfully - Used Data")
    void newTask_unsuccessful_case01() {
        NewTaskDTO data = mock(NewTaskDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.existsByTitle(data.title())).thenReturn(true);

        assertThrows(UsedDataException.class, () -> taskService.newTask(data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).existsByTitle(data.title());
        verify(taskRepository, never()).save(any(Task.class));
    }
    @Test
    @DisplayName("Creates Task Unsuccessfully - Invalid Deadline")
    void newTask_unsuccessful_case02() {
        NewTaskDTO data = mock(NewTaskDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.existsByTitle(data.title())).thenReturn(false);
        when(manager.getProject()).thenReturn(project);
        when(data.deadline()).thenReturn("01/12/2020 21:00");

        assertThrows(InvalidDeadlineException.class, () -> taskService.newTask(data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).existsByTitle(data.title());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Update Task Successfully")
    void updateTask_successful() {
        Long id = 1L;
        var data = mock(UpdateTaskDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        Task task = mock(Task.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findByTitle(data.title())).thenReturn(new HashSet<>());
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(new HashSet<>(){{add(task);}});
        when(data.deadline()).thenReturn("01/12/2030 21:00");

        assertDoesNotThrow(() -> taskService.updateTask(id, data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findByTitle(data.title());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    @Test
    @DisplayName("Update Task Unsuccessfully - Used Data")
    void updateTask_unsuccessful_case01() {
        Long id = 1L;
        var data = mock(UpdateTaskDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Task task = mock(Task.class);
        Set<Task> usedData = new HashSet<>() {{
            add(task);
        }};

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findByTitle(data.title())).thenReturn(usedData);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        assertThrows(UsedDataException.class, () -> taskService.updateTask(id, data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findByTitle(data.title());
        verify(taskRepository, never()).findById(id);
        verify(taskRepository, never()).save(any(Task.class));
    }
    @Test
    @DisplayName("Update Task Unsuccessfully - Non existent Task")
    void updateTask_unsuccessful_case02() {
        Long id = 1L;
        var data = mock(UpdateTaskDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        Set<Task> usedData = new HashSet<>();
        Task task = mock(Task.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findByTitle(data.title())).thenReturn(usedData);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(usedData);

        assertThrows(NullTaskException.class, () -> taskService.updateTask(id, data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findByTitle(data.title());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).save(any(Task.class));
    }
    @Test
    @DisplayName("Update Task Unsuccessfully - Invalid Deadline")
    void updateTask_unsuccessful_case03() {
        Long id = 1L;
        var data = mock(UpdateTaskDTO.class);
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        Task task = mock(Task.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findByTitle(data.title())).thenReturn(new HashSet<>());
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(new HashSet<>(){{add(task);}});
        when(data.deadline()).thenReturn("01/12/2020 21:00");

        assertThrows(InvalidDeadlineException.class, () -> taskService.updateTask(id, data, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findByTitle(data.title());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Delete Task Successfully")
    void deleteTask_successful() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        Task task = mock(Task.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(new HashSet<>(){{add(task);}});

        assertDoesNotThrow(() -> taskService.deleteTask(id, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).delete(any(Task.class));
    }
    @Test
    @DisplayName("Delete Task Unsuccessfully - Non existent Task")
    void deleteTask_unsuccessful_case01() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullTaskException.class, () -> taskService.deleteTask(id, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).delete(any(Task.class));
    }
    @Test
    @DisplayName("Delete Task Unsuccessfully - Isn't its Task")
    void deleteTask_unsuccessful_case02() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        Task task = mock(Task.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(new HashSet<>());

        assertThrows(NullTaskException.class, () -> taskService.deleteTask(id, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    @DisplayName("Get Tasks Successfully")
    void getTasks_successful() {
        var token = mock(JwtAuthenticationToken.class);
        Employee employee = mock(Employee.class);
        Project project = mock(Project.class);
        Task task = mock(Task.class);
        Set<Task> tasks = new HashSet<>() {{
            add(task);
        }};

        when(employeeService.getEmployee(token.getName())).thenReturn(employee);
        when(employee.getRole()).thenReturn(Role.MANAGER);
        when(employee.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(tasks);
        when(task.getDeadline()).thenReturn(LocalDateTime.now().plusDays(1L));
        when(task.getComments()).thenReturn(new HashSet<>());

        Set<TaskDTO> taskDTOs = assertDoesNotThrow(() -> taskService.getTasks(token));

        assertEquals(1, taskDTOs.size());
        verify(employeeService, times(1)).getEmployee(token.getName());
    }
    @Test
    @DisplayName("Get Tasks Unsuccessfully")
    void getTasks_unsuccessful() {
        var token = mock(JwtAuthenticationToken.class);
        Employee employee = mock(Employee.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(employee);
        when(employee.getRole()).thenReturn(Role.MANAGER);
        when(employee.getProject()).thenReturn(null);

        assertThrows(NullProjectException.class, () -> taskService.getTasks(token));

        verify(employeeService, times(1)).getEmployee(token.getName());
    }

    @Test
    @DisplayName("Get Task Successfully")
    void getTask_successful() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        Task task = mock(Task.class);
        Set<Task> tasks = new HashSet<>() {{
            add(task);
        }};

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(tasks);
        when(task.getDeadline()).thenReturn(LocalDateTime.now().plusDays(1L));
        when(task.getComments()).thenReturn(new HashSet<>());

        TaskDTO taskDTO = assertDoesNotThrow(() -> taskService.getTask(id, token));

        assertNotNull(taskDTO);
        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Get Task Unsuccessfully - Non existent Task")
    void getTask_unsuccessful_case01() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NullTaskException.class, () -> taskService.getTask(id, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("Get Task Unsuccessfully - Isn't its Task")
    void getTask_unsuccessful_case02() {
        Long id = 1L;
        var token = mock(JwtAuthenticationToken.class);
        Employee manager = mock(Employee.class);
        Project project = mock(Project.class);
        Task task = mock(Task.class);

        when(employeeService.getEmployee(token.getName())).thenReturn(manager);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(manager.getProject()).thenReturn(project);
        when(project.getTasks()).thenReturn(new HashSet<>());

        assertThrows(NullTaskException.class, () -> taskService.getTask(id, token));

        verify(employeeService, times(1)).getEmployee(token.getName());
        verify(taskRepository, times(1)).findById(id);
    }
}