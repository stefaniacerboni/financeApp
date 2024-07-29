package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.CategoryView;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    @Mock
    private CategoryService categoryService;
    @Mock
    private CategoryView categoryView;
    @Mock
    private JButton addCategoryButton;
    @Mock
    private JButton deleteCategoryButton;
    @Mock
    private JTable categoryTable;
    @Mock
    private ListSelectionModel selectionModel;

    @InjectMocks
    private CategoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(categoryView.getAddCategoryButton()).thenReturn(addCategoryButton);
        controller = new CategoryController(categoryService, categoryView);
        controller.initView();
    }

    @Test
    void shouldInitializeView() {
        verify(categoryView).getAddCategoryButton();
        verify(categoryView).getDeleteCategoryButton();
        verify(categoryService).getAllCategories();  // Assuming that loadCategories() is called in initView()
    }

    @Test
    void shouldAddCategorySuccessfully() {
        when(categoryView.getName()).thenReturn("New Category");
        when(categoryView.getDescription()).thenReturn("New Description");
        Category newCategory = new Category("New Category", "New Description");
        when(categoryService.addCategory(any(Category.class))).thenReturn(newCategory);

        controller.addCategory();

        verify(categoryService).addCategory(newCategory);
        verify(categoryView).addCategoryToTable(newCategory);
        verify(categoryView).setStatus("Category added successfully.");
        verify(categoryView).clearForm();
    }

    @Test
    void shouldHandleAddCategoryFailure() {
        when(categoryView.getName()).thenReturn("New Category");
        when(categoryView.getDescription()).thenReturn("New Description");
        when(categoryService.addCategory(any(Category.class))).thenReturn(null);

        controller.addCategory();

        verify(categoryView).setStatus("Failed to add category.");
    }

    @Test
    void shouldNotDeleteIfNoCategorySelected() {
        when(categoryView.getSelectedCategoryIndex()).thenReturn(-1);

        controller.deleteCategory();

        verify(categoryView, never()).getCategoryIdFromTable(anyInt());
        verify(categoryService, never()).deleteCategory(anyLong());
        verify(categoryView).setStatus("No category selected for deletion.");
    }
}
