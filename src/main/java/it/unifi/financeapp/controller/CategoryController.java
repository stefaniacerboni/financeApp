package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.CategoryView;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.exceptions.InvalidCategoryException;

public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryView categoryView;

    public CategoryController(CategoryService categoryService, CategoryView categoryView) {
        this.categoryService = categoryService;
        this.categoryView = categoryView;
    }

    public void initView() {
        loadCategories();
    }

    void loadCategories() {
        java.util.List<Category> categories = categoryService.getAllCategories();
        categories.forEach(categoryView::addCategoryToTable);
    }

    public void addCategory() {
        Category category = new Category(categoryView.getName(), categoryView.getDescription());
        Category result = categoryService.addCategory(category);
        if (result != null) {
            categoryView.addCategoryToTable(result);
            categoryView.setStatus("Category added successfully.");
            categoryView.clearForm();
        } else {
            categoryView.setStatus("Failed to add category.");
        }
    }

    public void deleteCategory() {
        int selectedRow = categoryView.getSelectedCategoryIndex();
        if (selectedRow >= 0) {
            Long categoryId = categoryView.getCategoryIdFromTable(selectedRow);
            try {
                categoryService.deleteCategory(categoryId);
                categoryView.removeCategoryFromTable(selectedRow);
                categoryView.setStatus("Category deleted successfully.");
            } catch (InvalidCategoryException categoryException) {
                categoryView.setStatus(categoryException.getMessage());
            }
        } else {
            categoryView.setStatus("No category selected for deletion.");
        }
    }
}