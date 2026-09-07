package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Camada de serviço: concentra as regras de negócio do Gerenciador de Tarefas
 * e manipula a coleção de tarefas em memória (Java Collections API),
 * delegando a persistência ao {@link TaskRepository} injetado.
 *
 * <p>Esta classe é a fronteira entre a interface gráfica ({@code view}) e a
 * persistência ({@code repository}) — a {@code view} nunca deve acessar o
 * repositório diretamente.</p>
 */
public class TaskService {

    private final TaskRepository repository;
    private final List<Task> tasks;
    private final AtomicLong nextId;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
        this.tasks = new ArrayList<>(repository.loadTasks());

        long maxId = tasks.stream()
                .mapToLong(Task::getId)
                .max()
                .orElse(0L);
        this.nextId = new AtomicLong(maxId + 1);
    }

    /**
     * Cria e adiciona uma nova tarefa.
     *
     * @param description texto da tarefa (não pode ser nulo/vazio)
     * @return a tarefa criada
     * @throws IllegalArgumentException se a descrição for nula ou em branco
     */
    public Task addTask(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descrição da tarefa não pode estar vazia.");
        }

        Task task = new Task(nextId.getAndIncrement(), description.trim(), false, LocalDateTime.now());
        tasks.add(task);
        persist();
        return task;
    }

    /**
     * Marca a tarefa informada como concluída.
     *
     * @throws TaskNotFoundException se não existir tarefa com o ID informado
     */
    public void completeTask(long id) {
        Task task = findByIdOrThrow(id);
        task.setCompleted(true);
        persist();
    }

    /**
     * Remove a tarefa informada da coleção.
     *
     * @throws TaskNotFoundException se não existir tarefa com o ID informado
     */
    public void deleteTask(long id) {
        boolean removed = tasks.removeIf(t -> t.getId() == id);
        if (!removed) {
            throw new TaskNotFoundException(id);
        }
        persist();
    }

    /**
     * Retorna uma visão somente-leitura de todas as tarefas, ordenadas com as
     * pendentes primeiro (e, dentro de cada grupo, por ID crescente).
     */
    public List<Task> getAllTasks() {
        List<Task> sorted = new ArrayList<>(tasks);
        sorted.sort(Comparator.comparing(Task::isCompleted).thenComparing(Task::getId));
        return Collections.unmodifiableList(sorted);
    }

    public Optional<Task> findById(long id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public int countPending() {
        return (int) tasks.stream().filter(t -> !t.isCompleted()).count();
    }

    public int countCompleted() {
        return (int) tasks.stream().filter(Task::isCompleted).count();
    }

    /** Persiste explicitamente o estado atual (útil, por exemplo, ao fechar a janela). */
    public void persist() {
        repository.saveTasks(tasks);
    }

    private Task findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
