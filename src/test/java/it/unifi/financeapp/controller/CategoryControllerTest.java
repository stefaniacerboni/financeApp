package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.CategoryView;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

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

    @InjectMocks
    private CategoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(categoryView.getAddCategoryButton()).thenReturn(addCategoryButton);
        when(categoryView.getDeleteCategoryButton()).thenReturn(deleteCategoryButton);
        controller = new CategoryController(categoryService, categoryView);
        controller.initView();
    }

    @Test
    void shouldInitializeView() {
        verify(categoryView).getAddCategoryButton();
        verify(categoryView).getDeleteCategoryButton();
        verify(categoryService).getAllCategories();  // loadCategories() is called in initView()
    }

    @Test
    void testAddCategoryActionListener() {
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(addCategoryButton).addActionListener(captor.capture());
        ActionListener listener = captor.getValue();

        // Simulate the button click
        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        verify(categoryService).addCategory(any(Category.class));
    }

    @Test
    void testDeleteCategoryActionListener() {
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(deleteCategoryButton).addActionListener(captor.capture());
        ActionListener listener = captor.getValue();

        // Simulate the button click
        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        verify(categoryService).deleteCategory(any(Long.class));
    }

    @Test
    void testLoadCategoriesOnInit() {
        List<Category> mockCategories = Arrays.asList(new Category("1", "Food"), new Category("2", "Utilities"));
        when(categoryService.getAllCategories()).thenReturn(mockCategories);

        controller.initView();

        // Verify each category is added to the view
        mockCategories.forEach(category ->
                verify(categoryView).addCategoryToTable(category)
        );
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

    @Test
    void shouldDeleteSelectedCategory() {
        when(categoryView.getSelectedCategoryIndex()).thenReturn(0);
        when(categoryView.getCategoryIdFromTable(0)).thenReturn(1L);

        controller.deleteCategory();

        verify(categoryService).deleteCategory(1L);
        verify(categoryView).removeCategoryFromTable(0);
        verify(categoryView).setStatus("Category deleted successfully.");
    }
}
