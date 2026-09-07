package com.taskmanager.view;

import com.taskmanager.model.Task;
import com.taskmanager.repository.FileTaskRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;

/**
 * Janela principal (JFrame) do Gerenciador de Tarefas.
 *
 * <p>Responsável apenas por apresentação e interação com o usuário; toda a
 * regra de negócio fica delegada a {@link TaskService}.</p>
 */
public class MainFrame extends JFrame {

    private static final String FILE_NAME = "tasks.txt";

    private final TaskService taskService;
    private final TaskTableModel tableModel;
    private final JTable table;
    private final JTextField descriptionField;
    private final JLabel statusBar;

    public MainFrame() {
        super("Gerenciador de Tarefas");

        TaskRepository repository = new FileTaskRepository(Path.of(FILE_NAME));
        this.taskService = new TaskService(repository);
        this.tableModel = new TaskTableModel();
        this.table = new JTable(tableModel);
        this.descriptionField = new JTextField();
        this.statusBar = new JLabel();

        configureFrame();
        buildLayout();
        refreshTable();
        registerWindowListener();
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // salvamos manualmente antes de sair
        setSize(760, 480);
        setMinimumSize(new Dimension(620, 380));
        setLocationRelativeTo(null);
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        root.add(buildInputPanel(), BorderLayout.NORTH);
        root.add(buildTablePanel(), BorderLayout.CENTER);
        root.add(buildActionPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Nova Tarefa"));

        descriptionField.addActionListener(e -> addTask()); // permite adicionar com Enter

        JButton addButton = new JButton("Adicionar Tarefa");
        addButton.addActionListener(e -> addTask());

        panel.add(descriptionField, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane buildTablePanel() {
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(340);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        return new JScrollPane(table);
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JButton completeButton = new JButton("Concluir Tarefa Selecionada");
        completeButton.addActionListener(e -> completeSelectedTask());

        JButton deleteButton = new JButton("Excluir Tarefa Selecionada");
        deleteButton.addActionListener(e -> deleteSelectedTask());

        buttons.add(completeButton);
        buttons.add(deleteButton);

        panel.add(buttons, BorderLayout.WEST);
        panel.add(statusBar, BorderLayout.EAST);
        return panel;
    }

    /** Garante que as tarefas sejam salvas ao fechar a janela (botão X). */
    private void registerWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    private void handleExit() {
        try {
            taskService.persist();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível salvar as tarefas antes de sair:\n" + ex.getMessage(),
                    "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        } finally {
            dispose();
            System.exit(0);
        }
    }

    private void addTask() {
        String description = descriptionField.getText();
        try {
            taskService.addTask(description);
            descriptionField.setText("");
            refreshTable();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void completeSelectedTask() {
        Task selected = getSelectedTaskOrWarn();
        if (selected == null) {
            return;
        }

        if (selected.isCompleted()) {
            JOptionPane.showMessageDialog(this, "Essa tarefa já está concluída.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        taskService.completeTask(selected.getId());
        refreshTable();
    }

    private void deleteSelectedTask() {
        Task selected = getSelectedTaskOrWarn();
        if (selected == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a tarefa \"" + selected.getDescription() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            taskService.deleteTask(selected.getId());
            refreshTable();
        }
    }

    private Task getSelectedTaskOrWarn() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela primeiro.",
                    "Nenhuma tarefa selecionada", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return tableModel.getTaskAt(selectedRow);
    }

    private void refreshTable() {
        tableModel.setTasks(taskService.getAllTasks());
        statusBar.setText(String.format("Pendentes: %d   |   Concluídas: %d",
                taskService.countPending(), taskService.countCompleted()));
    }
}
