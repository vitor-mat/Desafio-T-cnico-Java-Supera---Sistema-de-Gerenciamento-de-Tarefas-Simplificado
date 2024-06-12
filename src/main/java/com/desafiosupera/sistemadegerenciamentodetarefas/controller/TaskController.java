package com.desafiosupera.sistemadegerenciamentodetarefas.controller;

import com.desafiosupera.sistemadegerenciamentodetarefas.DTO.task.TaskDTO;
import com.desafiosupera.sistemadegerenciamentodetarefas.DTO.task.TaskStatusDTO;
import com.desafiosupera.sistemadegerenciamentodetarefas.DTO.task.TaskUpdateDTO;
import com.desafiosupera.sistemadegerenciamentodetarefas.service.TaskService;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.Task;
import com.desafiosupera.sistemadegerenciamentodetarefas.enums.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> findAll() {
        List<Task> tasks = taskService.findAll();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable Long id) {
        taskService.verifyUserPermition(id);

        Task task = taskService.getById(id);
        TaskDTO taskDTO = new TaskDTO(task.getId(), task.getTitulo(), task.getDescricao(), task.getStatus(), task.getDataCriacao());

        return ResponseEntity.ok(taskDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable Long id, @RequestBody TaskUpdateDTO taskUpdateDTO) {
        taskService.verifyUserPermition(id);
        taskService.updateTask(id, taskUpdateDTO);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatusTask(@PathVariable Long id, @RequestBody TaskStatusDTO statusRequest) {
        TaskStatus taskStatus = taskService.parseStatusRequest(statusRequest.taskStatus());

        taskService.verifyUserPermition(id);
        taskService.updateStatusTask(id, taskStatus);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deteleTask(@PathVariable Long id) {
        taskService.verifyUserPermition(id);
        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Task> registerTask(@Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        Task newTask = taskService.registerTask(taskUpdateDTO);
        return ResponseEntity.ok(newTask);
    }
}
