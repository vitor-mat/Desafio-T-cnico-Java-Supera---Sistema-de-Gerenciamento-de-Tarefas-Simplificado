package com.desafiosupera.sistemadegerenciamentodetarefas.DTO.task;

import com.desafiosupera.sistemadegerenciamentodetarefas.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TaskDTO(Long id, String titulo, String descricao, TaskStatus taskStatus,  @JsonFormat(pattern = "yyyy-MM-dd") LocalDateTime dataCriacao) {
}
