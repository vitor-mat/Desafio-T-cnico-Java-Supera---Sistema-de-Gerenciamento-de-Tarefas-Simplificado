package com.desafiosupera.sistemadegerenciamentodetarefas.exception;

public class InvalidTaskStatusException extends RuntimeException {
    public InvalidTaskStatusException(String message) {
        super(message);
    }
}