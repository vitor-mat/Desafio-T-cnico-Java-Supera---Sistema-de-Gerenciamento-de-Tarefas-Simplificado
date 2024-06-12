package com.desafiosupera.sistemadegerenciamentodetarefas.repository;

import com.desafiosupera.sistemadegerenciamentodetarefas.enums.TaskStatus;
import com.desafiosupera.sistemadegerenciamentodetarefas.enums.UserRole;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.Task;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.desafiosupera.sistemadegerenciamentodetarefas.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("userName2", new BCryptPasswordEncoder().encode("456"), UserRole.USER);
        //user = userRepository.save(user);

        Task task1 = new Task("Task 1", "Description 1", TaskStatus.PENDENTE, user);
        task1.setDataCriacao(LocalDateTime.now().minusDays(1));

        Task task2 = new Task("Task 2", "Description 2", TaskStatus.CONCLUIDA, user);
        task2.setDataCriacao(LocalDateTime.now());

        entityManager.persist(task1);
        entityManager.persist(task2);
    }



    @Test
    void findAllOrderByDataCriacaoDesc() {
        List<Task> tasks = taskRepository.findAllOrderByDataCriacaoDesc();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals("Task 2", tasks.get(0).getTitulo());
        assertEquals("Task 1", tasks.get(1).getTitulo());
    }

    /*
     @Test
    void findByCriadorAndStatus() {
        List<Task> tasks = taskRepository.findByCriadorAndStatus(user, TaskStatus.PENDENTE);
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitulo());
    }
    * */
}