package com.taskmanager.view;

import com.taskmanager.model.Task;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link AbstractTableModel} responsável por adaptar uma {@link List} de
 * {@link Task} para exibição no {@code JTable} da interface gráfica, com as
 * colunas: Status, ID, Descrição e Data de Criação.
 */
public class TaskTableModel extends AbstractTableModel {

    private final String[] columns = {"Status", "ID", "Descrição", "Data de Criação"};
    private List<Task> tasks = new ArrayList<>();

    /** Substitui os dados exibidos e notifica a tabela para redesenhar. */
    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        fireTableDataChanged();
    }

    public Task getTaskAt(int rowIndex) {
        return tasks.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return task.getStatusLabel();
            case 1:
                return task.getId();
            case 2:
                return task.getDescription();
            case 3:
                return task.getCreatedAtFormatted();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 1 ? Long.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // tabela é somente-leitura; edição ocorre via botões/diálogos
    }
}
