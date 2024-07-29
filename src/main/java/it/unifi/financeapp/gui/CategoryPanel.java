package it.unifi.financeapp.gui;


import it.unifi.financeapp.model.Category;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CategoryPanel extends JPanel implements CategoryView {
    private JTextField nameField, descriptionField;
    private JButton addCategoryButton, deleteCategoryButton;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public CategoryPanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    void initUI() {
        // Top Panel for form
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.setName("formPanel");
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        nameField.setName("nameField");
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        descriptionField.setName("descriptionField");
        formPanel.add(descriptionField);

        //Add Button
        addCategoryButton = new JButton("Add Category");
        addCategoryButton.setEnabled(false);
        addCategoryButton.setName("addCategoryButton");
        formPanel.add(addCategoryButton);

        //Table of Categories
        String[] columnNames = {"Id", "Name", "Description"};
        tableModel = new DefaultTableModel(null, columnNames);
        categoryTable = new JTable(tableModel);
        categoryTable.setName("categoryTable");
        JScrollPane scrollPane = new JScrollPane(categoryTable);

        //Delete Button
        deleteCategoryButton = new JButton("Delete Selected");
        deleteCategoryButton.setEnabled(false);
        deleteCategoryButton.setName("deleteCategoryButton");
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(deleteCategoryButton);

        //Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        southPanel.add(statusLabel);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        attachDocumentListeners();
    }

    private void attachDocumentListeners() {
        DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                //
            }

            public void removeUpdate(DocumentEvent e) {
                checkFields();
            }

            public void insertUpdate(DocumentEvent e) {
                checkFields();
            }
        };
        nameField.getDocument().addDocumentListener(listener);
        descriptionField.getDocument().addDocumentListener(listener);
    }

    /**
     * Enables the Add button only if both name and description fields are not empty.
     */
    void checkFields() {
        boolean enabled = !nameField.getText().trim().isEmpty() && !descriptionField.getText().trim().isEmpty();
        addCategoryButton.setEnabled(enabled);
    }

    @Override
    public String getName() {
        return nameField.getText();
    }

    @Override
    public void setName(String name) {
        nameField.setText(name);
    }

    @Override
    public String getDescription() {
        return descriptionField.getText();
    }

    @Override
    public void setDescription(String description) {
        descriptionField.setText(description);
    }

    @Override
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void clearForm() {
        nameField.setText("");
        descriptionField.setText("");
    }

    @Override
    public void addCategoryToTable(Category category) {
        tableModel.addRow(new Object[]{category.getId(), category.getName(), category.getDescription()});
    }

    @Override
    public void removeCategoryFromTable(int rowIndex) {
        tableModel.removeRow(rowIndex);
    }

    @Override
    public int getSelectedCategoryIndex() {
        return categoryTable.getSelectedRow();
    }

    @Override
    public Long getCategoryIdFromTable(int rowIndex) {
        return (Long) tableModel.getValueAt(rowIndex, 0);
    }

    @Override
    public JButton getAddCategoryButton() {
        return addCategoryButton;
    }

    @Override
    public JButton getDeleteCategoryButton() {
        return deleteCategoryButton;
    }

    public JTable getCategoryTable() {
        return categoryTable;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getDescriptionField() {
        return descriptionField;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }
}
