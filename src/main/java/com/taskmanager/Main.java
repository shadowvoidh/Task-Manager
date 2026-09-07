package com.taskmanager;

import com.taskmanager.view.MainFrame;

import javax.swing.*;

/**
 * Ponto de entrada da aplicação Gerenciador de Tarefas.
 *
 * <p>Toda a construção e exibição da interface Swing ocorre dentro do
 * Event Dispatch Thread (EDT), via {@link SwingUtilities#invokeLater}.</p>
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Se o look and feel do sistema não estiver disponível, segue com o padrão do Swing.
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
