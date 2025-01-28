package it.unifi.financeapp.gui;


import it.unifi.financeapp.controller.CategoryController;
import it.unifi.financeapp.model.Category;

import javax.swing.*;
import java.awt.*;

public class CategoryPanel extends BasePanel implements CategoryView {

    private static final long serialVersionUID = 1L;

    private JTextField nameField;
    private JTextField descriptionField;
    private transient CategoryController categoryController;

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

        addButton = createAddButton("Category");
        formPanel.add(addButton);

        deleteButton.setText("Delete Category");

        addButton.addActionListener(e -> categoryController.addCategory());
        deleteButton.addActionListener(e -> categoryController.deleteCategory());

        return formPanel;
    }

    public void setCategoryController(CategoryController categoryController) {
        this.categoryController = categoryController;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{"Id", "Name", "Description"};
    }

    @Override
    protected void attachDocumentListeners() {
        attachDocumentListeners(nameField, descriptionField);
    }

    /**
     * Enables the Add button only if both name and description fields are not empty.
     */
    @Override
    protected void checkFields() {
        boolean enabled = !nameField.getText().trim().isEmpty() && !descriptionField.getText().trim().isEmpty();
        addButton.setEnabled(enabled);
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

