package com.desafiosupera.sistemadegerenciamentodetarefas.repository;

import com.desafiosupera.sistemadegerenciamentodetarefas.model.Task;
import com.desafiosupera.sistemadegerenciamentodetarefas.model.User;
import com.desafiosupera.sistemadegerenciamentodetarefas.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t ORDER BY t.dataCriacao DESC")
    List<Task> findAllOrderByDataCriacaoDesc();

    List<Task> findByCriadorAndStatus(User criador, TaskStatus status);
}
