package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários da camada de serviço {@link TaskService}.
 *
 * <p>Utiliza um {@link TaskRepository} em memória (fake), evitando qualquer
 * acesso a disco durante os testes — mantendo-os rápidos, isolados e
 * repetíveis.</p>
 */
class TaskServiceTest {

    private InMemoryTaskRepository repository;
    private TaskService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
        service = new TaskService(repository);
    }

    @Test
    @DisplayName("Deve adicionar uma nova tarefa com sucesso")
    void deveAdicionarTarefa() {
        Task task = service.addTask("Estudar Java");

        assertNotNull(task);
        assertEquals("Estudar Java", task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals(1, service.getAllTasks().size());
    }

    @Test
    @DisplayName("Não deve adicionar tarefa com descrição nula ou em branco")
    void naoDeveAdicionarTarefaComDescricaoInvalida() {
        assertThrows(IllegalArgumentException.class, () -> service.addTask(""));
        assertThrows(IllegalArgumentException.class, () -> service.addTask("   "));
        assertThrows(IllegalArgumentException.class, () -> service.addTask(null));
    }

    @Test
    @DisplayName("Deve marcar uma tarefa existente como concluída")
    void deveConcluirTarefa() {
        Task task = service.addTask("Lavar o carro");

        service.completeTask(task.getId());

        assertTrue(service.findById(task.getId()).orElseThrow().isCompleted());
        assertEquals(1, service.countCompleted());
        assertEquals(0, service.countPending());
    }

    @Test
    @DisplayName("Deve lançar TaskNotFoundException ao concluir tarefa inexistente")
    void deveLancarExcecaoAoConcluirTarefaInexistente() {
        assertThrows(TaskNotFoundException.class, () -> service.completeTask(999L));
    }

    @Test
    @DisplayName("Deve excluir uma tarefa existente")
    void deveExcluirTarefa() {
        Task task = service.addTask("Tarefa temporária");

        service.deleteTask(task.getId());

        assertTrue(service.getAllTasks().isEmpty());
        assertTrue(service.findById(task.getId()).isEmpty());
    }

    @Test
    @DisplayName("Deve lançar TaskNotFoundException ao excluir tarefa inexistente")
    void deveLancarExcecaoAoExcluirTarefaInexistente() {
        assertThrows(TaskNotFoundException.class, () -> service.deleteTask(123L));
    }

    @Test
    @DisplayName("Deve listar tarefas pendentes antes das concluídas")
    void deveOrdenarTarefasPendentesPrimeiro() {
        Task t1 = service.addTask("Primeira");
        Task t2 = service.addTask("Segunda");
        service.completeTask(t1.getId());

        List<Task> all = service.getAllTasks();

        assertFalse(all.get(0).isCompleted());
        assertEquals(t2.getId(), all.get(0).getId());
        assertTrue(all.get(1).isCompleted());
        assertEquals(t1.getId(), all.get(1).getId());
    }

    @Test
    @DisplayName("Deve persistir as tarefas no repositório a cada alteração")
    void devePersistirTarefasNoRepositorio() {
        service.addTask("Persistir isso");

        assertEquals(1, repository.getSavedTasks().size());
    }

    @Test
    @DisplayName("Deve carregar tarefas previamente existentes no repositório ao iniciar")
    void deveCarregarTarefasExistentesAoIniciar() {
        InMemoryTaskRepository repositoryComDados = new InMemoryTaskRepository();
        repositoryComDados.saveTasks(List.of(new Task(1, "Tarefa antiga")));

        TaskService serviceComDados = new TaskService(repositoryComDados);

        assertEquals(1, serviceComDados.getAllTasks().size());
        // Garante que o próximo ID gerado não colide com o ID já existente (1)
        Task novaTarefa = serviceComDados.addTask("Tarefa nova");
        assertEquals(2, novaTarefa.getId());
    }

    /**
     * Implementação em memória de {@link TaskRepository}, usada exclusivamente
     * nos testes para evitar qualquer acesso a disco.
     */
    private static class InMemoryTaskRepository implements TaskRepository {
        private List<Task> savedTasks = new ArrayList<>();

        @Override
        public List<Task> loadTasks() {
            return new ArrayList<>(savedTasks);
        }

        @Override
        public void saveTasks(List<Task> tasks) {
            this.savedTasks = new ArrayList<>(tasks);
        }

        List<Task> getSavedTasks() {
            return savedTasks;
        }
    }
}
