package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.CategoryView;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.exceptions.InvalidCategoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    @Mock
    private CategoryService categoryService;
    @Mock
    private CategoryView categoryView;

    @InjectMocks
    private CategoryController controller;

    @BeforeEach
    void setUp() {
        controller = new CategoryController(categoryService, categoryView);
        controller.initView();
    }

    @Test
    void shouldInitializeView() {
        verify(categoryService).getAllCategories();  // loadCategories() is called in initView()
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
    void testAddCategorySuccessfully() {
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
    void testAddCategoryFailure() {
        when(categoryView.getName()).thenReturn("New Category");
        when(categoryView.getDescription()).thenReturn("New Description");
        when(categoryService.addCategory(any(Category.class))).thenReturn(null);

        controller.addCategory();

        verify(categoryView).setStatus("Failed to add category.");
    }

    @Test
    void testNotDeleteIfNoCategorySelected() {
        when(categoryView.getSelectedCategoryIndex()).thenReturn(-1);

        controller.deleteCategory();

        verify(categoryView, never()).getCategoryIdFromTable(anyInt());
        verify(categoryService, never()).deleteCategory(anyLong());
        verify(categoryView).setStatus("No category selected for deletion.");
    }

    @Test
    void testNotDeleteIfCategoryHasDependencies() {
        Long id = 1L;
        when(categoryView.getSelectedCategoryIndex()).thenReturn(0);
        when(categoryView.getCategoryIdFromTable(0)).thenReturn(id);
        doThrow(new InvalidCategoryException("Cannot delete category with existing expenses"))
                .when(categoryService).deleteCategory(id);

        controller.deleteCategory();

        verify(categoryView).setStatus("Cannot delete category with existing expenses");
    }

    @Test
    void testDeleteSelectedCategory() {
        when(categoryView.getSelectedCategoryIndex()).thenReturn(0);
        when(categoryView.getCategoryIdFromTable(0)).thenReturn(1L);

        controller.deleteCategory();

        verify(categoryService).deleteCategory(1L);
        verify(categoryView).removeCategoryFromTable(0);
        verify(categoryView).setStatus("Category deleted successfully.");
    }
}
