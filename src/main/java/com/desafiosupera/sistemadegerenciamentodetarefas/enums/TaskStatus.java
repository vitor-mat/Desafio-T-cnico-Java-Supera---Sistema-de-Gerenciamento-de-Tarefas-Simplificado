package com.desafiosupera.sistemadegerenciamentodetarefas.enums;

public enum TaskStatus {
    PENDENTE("PENDENTE"),
    EM_ANDAMENTO("EM_ANDAMENTO"),
    CONCLUIDA("CONCLUIDA");

    private final String code;

    TaskStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
