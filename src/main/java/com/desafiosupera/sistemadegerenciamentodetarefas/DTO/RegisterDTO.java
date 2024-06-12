package com.desafiosupera.sistemadegerenciamentodetarefas.DTO;

import com.desafiosupera.sistemadegerenciamentodetarefas.enums.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
