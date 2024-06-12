package com.desafiosupera.sistemadegerenciamentodetarefas.service;

import com.desafiosupera.sistemadegerenciamentodetarefas.DTO.task.TaskUpdateDTO;
import com.desafiosupera.sistemadegerenciamentodetarefas.exception.InvalidTaskStatusException;
import com.desafiosupera.sistemadegerenciamentodetarefas.exception.NotFoundException;
import com.desafiosupera.sistemadegerenciamentodetarefas.exception.StandarTaskException;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.Task;
import com.desafiosupera.sistemadegerenciamentodetarefas.repository.TaskRepository;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.User;
import com.desafiosupera.sistemadegerenciamentodetarefas.enums.TaskStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAllOrderByDataCriacaoDesc();
    }

    public Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Tarefa não encontrada"));
    }

    public void updateTask(Long id, TaskUpdateDTO taskUpdateDTO){
        Task task = getById(id);

        task.setTitulo(taskUpdateDTO.titulo());
        task.setDescricao(taskUpdateDTO.descricao());

        taskRepository.save(task);
    }

    public void updateStatusTask(Long id, TaskStatus taskStatus){
        Task task = getById(id);

        if (userHasTaskInProgress(task) && TaskStatus.EM_ANDAMENTO.equals(taskStatus)) throw new InvalidTaskStatusException("Você já possui uma tarefa em atendimento!");

        task.setStatus(taskStatus);
        taskRepository.save(task);
    }

    public void deleteTask (Long id) {
        Task task = getById(id);
        taskRepository.delete(task);
    }

    public boolean verifyUserPermition (Long id){
        Task task = getById(id);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (
                !task.getCriador().equals(currentUser) &&
                        !currentUser.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
        ) {
            throw new AccessDeniedException("Você não tem permissão para executar está ação!");
        };

        return true;
    }

    public Task registerTask(TaskUpdateDTO taskUpdateDTO) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Task> pendingTasks = taskRepository.findByCriadorAndStatus(currentUser, TaskStatus.PENDENTE);

        int maxTasksPending = 10;
        int minTaskTitleLength = 5;
        String errorMessage = taskUpdateDTO.titulo().length() < minTaskTitleLength ? "O titulo deve ter pelo menos 5 caracteres!" : "Você não pode ter mais de 10 tarefas pendentes!";

        if(pendingTasks.size() == maxTasksPending || taskUpdateDTO.titulo().length() < minTaskTitleLength) throw new StandarTaskException(errorMessage);

        Task newTask = new Task(taskUpdateDTO.titulo(), taskUpdateDTO.descricao(), TaskStatus.PENDENTE, currentUser);

        return taskRepository.save(newTask);
    }

    public boolean userHasTaskInProgress(Task task) {
        List<Task> tasksInProgress = taskRepository.findByCriadorAndStatus(task.getCriador(), TaskStatus.EM_ANDAMENTO);

        return !tasksInProgress.isEmpty();
    }

    public TaskStatus parseStatusRequest (String statusRequest) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getCode().equalsIgnoreCase(statusRequest)) {
                return status;
            }
        }
        throw new InvalidTaskStatusException("O Status enviado está inválido: " + statusRequest + ". Envie um status válido para fazer a alteração.");
    }
}