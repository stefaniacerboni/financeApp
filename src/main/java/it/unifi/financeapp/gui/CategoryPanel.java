package it.unifi.financeapp.gui;


import it.unifi.financeapp.model.Category;

import javax.swing.*;
import java.awt.*;

public class CategoryPanel extends BasePanel implements CategoryView {
    private JTextField nameField;
    private JTextField descriptionField;

    public CategoryPanel() {
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        nameField.setName("nameField");
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        descriptionField.setName("descriptionField");
        formPanel.add(descriptionField);

        addButton = new JButton("Add Category");
        addButton.setEnabled(false);
        formPanel.add(addButton);

        return formPanel;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{"Id", "Name", "Description"};
    }

    @Override
    public void attachDocumentListeners() {
        nameField.getDocument().addDocumentListener(listener);
        descriptionField.getDocument().addDocumentListener(listener);
    }

    /**
     * Enables the Add button only if both name and description fields are not empty.
     */
    @Override
    public void checkFields() {
        boolean enabled = !nameField.getText().trim().isEmpty() && !descriptionField.getText().trim().isEmpty();
        addButton.setEnabled(enabled);
    }

    @Override
    public void updateDeleteButtonEnabledState() {
        boolean isSelected = getSelectedCategoryIndex() >= 0;
        deleteButton.setEnabled(isSelected);
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
        return entityTable.getSelectedRow();
    }

    @Override
    public Long getCategoryIdFromTable(int rowIndex) {
        return (Long) tableModel.getValueAt(rowIndex, 0);
    }

    @Override
    public JButton getAddCategoryButton() {
        return addButton;
    }

    @Override
    public JButton getDeleteCategoryButton() {
        return deleteButton;
    }

    public JTable getCategoryTable() {
        return entityTable;
    }
}

