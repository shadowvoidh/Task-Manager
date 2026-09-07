package com.taskmanager.repository;

import com.taskmanager.model.Task;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação de {@link TaskRepository} que persiste as tarefas em um
 * arquivo de texto simples, utilizando a API {@code java.nio.file}
 * ({@link Files} e {@link Path}).
 *
 * <p>Cada linha do arquivo representa uma tarefa no formato:</p>
 * <pre>id|completed|createdAt|descrição</pre>
 *
 * <p><b>Observação:</b> por simplicidade, o caractere delimitador {@code '|'}
 * é substituído por {@code '/'} dentro da descrição antes de salvar, evitando
 * que o parser interprete incorretamente o conteúdo da tarefa.</p>
 */
public class FileTaskRepository implements TaskRepository {

    private static final String DELIMITER = "|";
    private static final String DELIMITER_REGEX = "\\|";

    private final Path filePath;

    public FileTaskRepository(Path filePath) {
        this.filePath = filePath;
    }

    public FileTaskRepository(String fileName) {
        this(Path.of(fileName));
    }

    @Override
    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks; // primeira execução: ainda não existe arquivo, retorna lista vazia
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                parseLine(line).ifPresent(tasks::add);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao carregar tarefas do arquivo: " + filePath, e);
        }

        return tasks;
    }

    @Override
    public void saveTasks(List<Task> tasks) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            List<String> lines = tasks.stream()
                    .map(this::toLine)
                    .collect(Collectors.toList());
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao salvar tarefas no arquivo: " + filePath, e);
        }
    }

    private String toLine(Task task) {
        String safeDescription = task.getDescription()
                .replace("|", "/")
                .replace("\n", " ")
                .replace("\r", " ");

        return task.getId() + DELIMITER
                + task.isCompleted() + DELIMITER
                + task.getCreatedAt() + DELIMITER
                + safeDescription;
    }

    private java.util.Optional<Task> parseLine(String line) {
        String[] parts = line.split(DELIMITER_REGEX, 4);
        if (parts.length < 4) {
            return java.util.Optional.empty(); // linha corrompida/inesperada: ignora
        }
        try {
            long id = Long.parseLong(parts[0].trim());
            boolean completed = Boolean.parseBoolean(parts[1].trim());
            LocalDateTime createdAt = LocalDateTime.parse(parts[2].trim());
            String description = parts[3];
            return java.util.Optional.of(new Task(id, description, completed, createdAt));
        } catch (RuntimeException e) {
            return java.util.Optional.empty(); // linha corrompida: ignora e segue carregando as demais
        }
    }
}
