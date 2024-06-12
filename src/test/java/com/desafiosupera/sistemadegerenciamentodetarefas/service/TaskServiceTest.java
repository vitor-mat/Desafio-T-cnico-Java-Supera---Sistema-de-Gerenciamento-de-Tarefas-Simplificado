package com.desafiosupera.sistemadegerenciamentodetarefas.service;

import com.desafiosupera.sistemadegerenciamentodetarefas.DTO.task.TaskUpdateDTO;
import com.desafiosupera.sistemadegerenciamentodetarefas.enums.TaskStatus;
import com.desafiosupera.sistemadegerenciamentodetarefas.enums.UserRole;
import com.desafiosupera.sistemadegerenciamentodetarefas.exception.InvalidTaskStatusException;
import com.desafiosupera.sistemadegerenciamentodetarefas.exception.NotFoundException;
import com.desafiosupera.sistemadegerenciamentodetarefas.exception.StandarTaskException;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.Task;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.User;
import com.desafiosupera.sistemadegerenciamentodetarefas.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;
    @Test
    @DisplayName("Should be able to find all tasks")
    void findAll_WhenTasksFound() {
        List<Task> tasks = Arrays.asList(
                new Task("Task 1", "Description 1", TaskStatus.PENDENTE, null),
                new Task("Task 2", "Description 2", TaskStatus.CONCLUIDA, null)
        );

        when(taskRepository.findAllOrderByDataCriacaoDesc()).thenReturn(tasks);

        List<Task> foundTasks = taskService.findAll();

        assertEquals(tasks.size(), foundTasks.size());
        assertEquals(tasks.get(0).getTitulo(), foundTasks.get(0).getTitulo());
        assertEquals(tasks.get(1).getTitulo(), foundTasks.get(1).getTitulo());
    }

    @Test
    @DisplayName("Should return an empty list when no tasks are found")
    void findAll_WhenNoTasksFound() {
        when(taskRepository.findAllOrderByDataCriacaoDesc()).thenReturn(Collections.emptyList());

        List<Task> foundTasks = taskService.findAll();

        assertTrue(foundTasks.isEmpty());
    }

    @Test
    @DisplayName("Should return task when found by id")
    void findById_WhenTaskFound() {
        Long taskId = 1L;
        Task task = new Task("Task 1", "Description 1", TaskStatus.PENDENTE, null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getById(taskId);

        assertEquals(task, foundTask);
    }

    @Test
    @DisplayName("Should throw NotFoundException when task is not found by id")
    void findById_WhenTaskNotFound() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.getById(taskId));
    }

    @Test
    @DisplayName("Should update task successfully")
    void updateTask_Success() {
        Long taskId = 1L;
        TaskUpdateDTO updatedTaskDTO = new TaskUpdateDTO("Updated Title", "Updated Description");
        Task originalTask = new Task("Original Title", "Original Description", TaskStatus.PENDENTE, null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(originalTask));

        taskService.updateTask(taskId, updatedTaskDTO);

        verify(taskRepository).save(originalTask);
        assertEquals(updatedTaskDTO.titulo(), originalTask.getTitulo());
        assertEquals(updatedTaskDTO.descricao(), originalTask.getDescricao());
    }

    @Test
    @DisplayName("Should update task status successfully")
    void updateStatusTask_Success() {
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.CONCLUIDA;
        Task originalTask = new Task("Original Title", "Original Description", TaskStatus.PENDENTE, null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(originalTask));

        taskService.updateStatusTask(taskId, newStatus);

        verify(taskRepository).save(originalTask);
        assertEquals(newStatus, originalTask.getStatus());
    }

    @Test
    @DisplayName("Should throw InvalidTaskStatusException when user has another task in progress")
    void updateStatusTask_Fail() {
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.EM_ANDAMENTO;
        Task originalTask = new Task("Original Title", "Original Description", TaskStatus.PENDENTE, null);
        List<Task> tasksInProgress = Arrays.asList(
                new Task("Task in Progress", "Description", TaskStatus.EM_ANDAMENTO, originalTask.getCriador())
        );
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(originalTask));
        when(taskRepository.findByCriadorAndStatus(originalTask.getCriador(), TaskStatus.EM_ANDAMENTO)).thenReturn(tasksInProgress);

        assertThrows(InvalidTaskStatusException.class, () -> taskService.updateStatusTask(taskId, newStatus));
    }

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_Success() {
        Long taskId = 1L;
        Task taskToDelete = new Task("Task to Delete", "Description", TaskStatus.PENDENTE, null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskToDelete));

        taskService.deleteTask(taskId);

        verify(taskRepository).delete(taskToDelete);
    }

    @Test
    @DisplayName("Should throw NotFoundException when task to delete is not found")
    void deleteTask_WhenTaskNotFound() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.deleteTask(taskId));
    }

    @Test
    @DisplayName("Should return true when user has permission")
    void verifyUserPermition_Success() {
        User currentUser = new User("username", "password", UserRole.USER);
        Long taskId = 1L;
        Task task = new Task("Task 1", "Description 1", TaskStatus.PENDENTE, currentUser);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        boolean hasPermission = taskService.verifyUserPermition(taskId);

        assertTrue(hasPermission);
    }

    @Test
    @DisplayName("Should not throw exception when admin tries to access another user's task")
    void verifyUserPermition_SuccessForAdmin() {
        User adminUser = new User("admin", "password", UserRole.ADMIN);
        User taskOwner = new User("taskOwner", "password", UserRole.USER);
        Long taskId = 1L;
        Task task = new Task("Task 1", "Description 1", TaskStatus.PENDENTE, taskOwner);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(adminUser, null));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertDoesNotThrow(() -> taskService.verifyUserPermition(taskId));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when currentUser tries to access another user's task")
    void verifyUserPermition_FailForRegularUser() {
        User currentUser = new User("uuid1", "regularUser", "password", UserRole.USER);
        User taskOwner = new User("uuid2", "taskOwner", "password", UserRole.USER);
        Long taskId = 1L;
        Task task = new Task("Task 1", "Description 1", TaskStatus.PENDENTE, taskOwner);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class, () -> taskService.verifyUserPermition(taskId));
    }

    @Test
    @DisplayName("Should return true when user has a task in progress")
    void userHasTaskInProgress_True() {
        User currentUser = new User("username", "password", UserRole.USER);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));

        List<Task> tasksInProgress = Collections.singletonList(
                new Task("Task in Progress", "Description", TaskStatus.EM_ANDAMENTO, currentUser)
        );

        when(taskRepository.findByCriadorAndStatus(currentUser, TaskStatus.EM_ANDAMENTO)).thenReturn(tasksInProgress);

        Task taskInProgress = new Task("Task in Progress", "Description", TaskStatus.EM_ANDAMENTO, currentUser);

        assertTrue(taskService.userHasTaskInProgress(taskInProgress));
    }

    @Test
    @DisplayName("Should return false when user has no tasks in progress")
    void userHasTaskInProgress_False() {
        User currentUser = new User("username", "password", UserRole.USER);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));

        List<Task> tasksInProgress = Collections.emptyList();

        when(taskRepository.findByCriadorAndStatus(currentUser, TaskStatus.EM_ANDAMENTO)).thenReturn(tasksInProgress);

        Task pendingTask = new Task("Pending Task", "Description", TaskStatus.PENDENTE, currentUser);

        assertFalse(taskService.userHasTaskInProgress(pendingTask));
    }

    @Test
    @DisplayName("Should parse status request successfully")
    void parseStatusRequest_Success() {
        String statusRequest = "EM_ANDAMENTO";

        TaskStatus expectedStatus = TaskStatus.EM_ANDAMENTO;

        TaskStatus parsedStatus = taskService.parseStatusRequest(statusRequest);

        assertEquals(expectedStatus, parsedStatus);
    }

    @Test
    @DisplayName("Should throw InvalidTaskStatusException when status request is invalid")
    void parseStatusRequest_InvalidStatus() {
        String invalidStatusRequest = "INVALIDO";


        assertThrows(InvalidTaskStatusException.class, () -> taskService.parseStatusRequest(invalidStatusRequest));
    }

    @Test
    @DisplayName("Should successfully register a new task")
    void registerTask_Success() {
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO("Valid Title", "Description 1");
        User currentUser = new User("username", "password", UserRole.USER);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));

        when(taskRepository.findByCriadorAndStatus(currentUser, TaskStatus.PENDENTE)).thenReturn(Collections.emptyList());
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task registeredTask = taskService.registerTask(taskUpdateDTO);

        assertNotNull(registeredTask);
        assertEquals(taskUpdateDTO.titulo(), registeredTask.getTitulo());
        assertEquals(taskUpdateDTO.descricao(), registeredTask.getDescricao());
        assertEquals(TaskStatus.PENDENTE, registeredTask.getStatus());
        assertEquals(currentUser, registeredTask.getCriador());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw StandarTaskException when task title is too short")
    void registerTask_TitleTooShort() {
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO("abc", "Description 1");
        User currentUser = new User("username", "password", UserRole.USER);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));

        when(taskRepository.findByCriadorAndStatus(currentUser, TaskStatus.PENDENTE)).thenReturn(Collections.emptyList());

        StandarTaskException exception = assertThrows(StandarTaskException.class, () -> taskService.registerTask(taskUpdateDTO));

        assertEquals("O titulo deve ter pelo menos 5 caracteres!", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw StandarTaskException when user has too many pending tasks")
    void registerTask_TooManyPendingTasks() {
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO("Valid Title", "Description 1");
        User currentUser = new User("username", "password", UserRole.USER);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));

        List<Task> pendingTasks = Collections.nCopies(10, new Task("Pending Task", "Description", TaskStatus.PENDENTE, currentUser));
        when(taskRepository.findByCriadorAndStatus(currentUser, TaskStatus.PENDENTE)).thenReturn(pendingTasks);

        StandarTaskException exception = assertThrows(StandarTaskException.class, () -> taskService.registerTask(taskUpdateDTO));

        assertEquals("Você não pode ter mais de 10 tarefas pendentes!", exception.getMessage());
    }
}