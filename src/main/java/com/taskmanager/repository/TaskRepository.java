package com.taskmanager.repository;

import com.taskmanager.model.Task;

import java.util.List;

/**
 * Abstração da camada de persistência de tarefas.
 *
 * <p>Depender desta interface (em vez de uma implementação concreta) permite
 * trocar o mecanismo de armazenamento (arquivo texto, banco de dados, etc.)
 * sem impactar a camada de serviço, além de facilitar a criação de testes
 * unitários com repositórios "fake"/em memória.</p>
 */
public interface TaskRepository {

    /**
     * Carrega todas as tarefas persistidas.
     *
     * @return lista de tarefas (vazia caso não exista nada persistido ainda)
     */
    List<Task> loadTasks();

    /**
     * Persiste a lista de tarefas informada, substituindo o conteúdo
     * previamente armazenado.
     *
     * @param tasks lista completa e atual de tarefas a ser salva
     */
    void saveTasks(List<Task> tasks);
}
