package com.desafiosupera.sistemadegerenciamentodetarefas.DTO.task;

import jakarta.validation.constraints.NotBlank;

public record TaskUpdateDTO(
        @NotBlank(message = "O título não pode estar vazio") String titulo,
        @NotBlank(message = "A descrição não pode estar vazio") String descricao
) {
}
