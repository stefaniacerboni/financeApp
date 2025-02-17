package it.unifi.financeapp.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class BasePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    protected JButton addButton;
    protected JTable entityTable;
    protected JButton deleteButton;
    protected JLabel statusLabel;
    protected DefaultTableModel tableModel;

    protected BasePanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Table
        String[] columnNames = getColumnNames(); // Abstract method to get column names
        tableModel = new DefaultTableModel(null, columnNames);
        entityTable = new JTable(tableModel);
        entityTable.setName("entityTable");
        JScrollPane scrollPane = new JScrollPane(entityTable);

        // South Panel
        deleteButton = new JButton();
        deleteButton.setEnabled(false);
        deleteButton.setName("deleteButton");

        statusLabel = new JLabel(" ");
        statusLabel.setName("statusLabel");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(deleteButton);
        southPanel.add(statusLabel);

        // Form Panel (Abstract - defined by subclasses)
        JPanel formPanel = createFormPanel();

        // Add components to the panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Event listeners
        attachDocumentListeners();
        entityTable.getSelectionModel().addListSelectionListener(e -> updateDeleteButtonEnabledState());
    }

    protected void attachDocumentListeners(JTextField... fields) {
        for (JTextField field : fields) {
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Generated
                public void changedUpdate(DocumentEvent e) {
                    //Not triggered on plain TextFields
                }

                public void removeUpdate(DocumentEvent e) {
                    checkFields();
                }

                public void insertUpdate(DocumentEvent e) {
                    checkFields();
                }
            });
        }
    }

    protected abstract JPanel createFormPanel();

    protected JButton createAddButton(String label) {
        JButton button = new JButton("Add " + label);
        button.setName("addButton");
        button.setEnabled(false);
        return button;
    }


    protected abstract String[] getColumnNames();

    protected abstract void attachDocumentListeners();

    protected abstract void checkFields();

    protected void updateDeleteButtonEnabledState() {
        deleteButton.setEnabled(entityTable.getSelectedRow() != -1);
    }
}