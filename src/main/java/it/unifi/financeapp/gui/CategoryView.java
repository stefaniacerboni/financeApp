package it.unifi.financeapp.gui;

import it.unifi.financeapp.model.Category;

import javax.swing.*;

public interface CategoryView {
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    void setStatus(String status);

    void clearForm();

    void addCategoryToTable(Category category);

    void removeCategoryFromTable(int rowIndex);

    int getSelectedCategoryIndex();

    Long getCategoryIdFromTable(int rowIndex);

    JButton getAddCategoryButton();

    JButton getDeleteCategoryButton();

    JTable getCategoryTable();

    JTextPane getTextPane();
}
