package com.desafiosupera.sistemadegerenciamentodetarefas.configuration.initialization;

import com.desafiosupera.sistemadegerenciamentodetarefas.enums.TaskStatus;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.Task;
import com.desafiosupera.sistemadegerenciamentodetarefas.repository.TaskRepository;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.User;
import com.desafiosupera.sistemadegerenciamentodetarefas.repository.UserRepository;
import com.desafiosupera.sistemadegerenciamentodetarefas.util.Constants;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

@org.springframework.context.annotation.Configuration
public class Configuration implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public Configuration(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public List<User> insertUsers() {
        return userRepository.saveAll(List.of(Constants.USER_1, Constants.USER_2));
    }

    public void inserTaks(List<User> users) {
        for (User user : users) {
            Task task1 = new Task("Título da tarefa " + user.getId(), "Descrição da tarefa " + user.getId(), TaskStatus.CONCLUIDA, user);
            Task task2 = new Task("Título da tarefa " + user.getId() + 1, "Descrição da tarefa " + user.getId() + 1, TaskStatus.CONCLUIDA, user);
            taskRepository.saveAll(List.of(task1, task2));
        }
    }

    ;

    @Override
    public void run(String... args) throws Exception {
        List<User> users = insertUsers();
        inserTaks(users);
    }
}
