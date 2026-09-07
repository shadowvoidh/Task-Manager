package com.taskmanager.service;

/**
 * Lançada quando se tenta operar (concluir, excluir, etc.) sobre uma tarefa
 * cujo ID não existe na coleção atual.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(long id) {
        super("Tarefa com ID " + id + " não encontrada.");
    }
}
