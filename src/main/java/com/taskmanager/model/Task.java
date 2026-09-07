package com.taskmanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Representa uma tarefa do Gerenciador de Tarefas.
 *
 * <p>Esta é uma entidade simples (POJO) que carrega o estado de uma tarefa:
 * identificador único, descrição, status de conclusão e data/hora de criação.</p>
 */
public class Task {

    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final long id;
    private String description;
    private boolean completed;
    private final LocalDateTime createdAt;

    public Task(long id, String description, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.description = Objects.requireNonNull(description, "description não pode ser nula");
        this.completed = completed;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt não pode ser nulo");
    }

    /**
     * Construtor de conveniência para criação de uma nova tarefa (não concluída,
     * com data de criação igual ao momento atual).
     */
    public Task(long id, String description) {
        this(id, description, false, LocalDateTime.now());
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Objects.requireNonNull(description, "description não pode ser nula");
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtFormatted() {
        return createdAt.format(DATE_FORMATTER);
    }

    /** Rótulo amigável exibido na coluna "Status" da tabela. */
    public String getStatusLabel() {
        return completed ? "\u2714 Conclu\u00edda" : "\u25FB Pendente";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                '}';
    }
}
