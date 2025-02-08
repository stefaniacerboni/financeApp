package it.unifi.financeapp.service;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.service.exceptions.InvalidCategoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Nested
    @DisplayName("Happy Cases")
    class HappyCases {

        @Test
        void testAddCategory() {
            Category category = new Category("Name", "Description");
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            Category result = categoryService.addCategory(category);
            assertNotNull(result);
            assertEquals(category.getName(), result.getName());
            assertEquals(category.getDescription(), result.getDescription());
            verify(categoryRepository).save(category);
        }

        @Test
        void testSaveExistingCategory() {
            Category existingCategory = new Category("Name", "Description");
            existingCategory.setId(1L); // Simulate an existing category
            when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

            Category updatedCategory = categoryService.addCategory(existingCategory);

            assertNotNull(updatedCategory);
            assertEquals(existingCategory.getId(), updatedCategory.getId());
            verify(categoryRepository).save(existingCategory);
        }

        @Test
        void testFindCategoryById() {
            Category expectedCategory = new Category("categoryName", "description");
            when(categoryRepository.findById(expectedCategory.getId())).thenReturn(expectedCategory);
            Category result = categoryService.findCategoryById(expectedCategory.getId());
            assertNotNull(result);
            assertEquals(expectedCategory, result);
            verify(categoryRepository).findById(expectedCategory.getId());
        }

        @Test
        void testUpdateCategory() {
            Category originalCategory = new Category("originalName", "description");
            Category updatedCategory = new Category("updatedName", "description");
            when(categoryRepository.update(originalCategory)).thenReturn(updatedCategory);
            Category result = categoryService.updateCategory(originalCategory);
            assertNotNull(result);
            assertEquals(updatedCategory.getName(), result.getName());
            verify(categoryRepository).update(originalCategory);
        }


        @Test
        void testGetAllCategories() {
            List<Category> expectedCategories = Arrays.asList(new Category("name1", "description1"), new Category("name2", "description2"));
            when(categoryRepository.findAll()).thenReturn(expectedCategories);
            List<Category> actualCategories = categoryService.getAllCategories();
            assertNotNull(actualCategories);
            assertEquals(2, actualCategories.size());
            assertEquals(expectedCategories, actualCategories);
            verify(categoryRepository).findAll();
        }

        @Test
        void testGetAllCategoriesEmptyList() {
            when(categoryRepository.findAll()).thenReturn(List.of());
            List<Category> actualCategories = categoryService.getAllCategories();
            assertNotNull(actualCategories);
            assertTrue(actualCategories.isEmpty());
        }

        @Test
        void testDeleteCategory() {
            Category category = new Category("name", "description");
            when(categoryRepository.findById(category.getId())).thenReturn(category);
            categoryService.deleteCategory(category.getId());
            verify(categoryRepository).findById(category.getId());
            verify(categoryRepository).delete(category);
        }

        @Test
        void testDeleteAll() {
            categoryService.deleteAll();
            verify(categoryRepository).deleteAll();
        }
    }

    @Nested
    @DisplayName("Error Cases")
    class ErrorCases {

        @Test
        void testAddCategoryDatabaseError() {
            Category newCategory = new Category("newName", "newDescription");
            when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException("Database error"));
            RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryService.addCategory(newCategory));
            assertTrue(exception.getMessage().contains("Database error"));
        }

        @Test
        void testAddNullCategoryThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(null));
        }

        @Test
        void testAddCategoryThrowsPersistenceException() {
            Category category = new Category("name", "description");
            doThrow(new PersistenceException("Could not persist category")).when(categoryRepository).save(any(Category.class));
            assertThrows(PersistenceException.class, () -> categoryService.addCategory(category));
        }

        @Test
        void testAddCategoryWithInvalidData() {
            Category invalidCategory = new Category("", "  ");
            assertThrows(InvalidCategoryException.class, () -> categoryService.addCategory(invalidCategory));
        }

        @Test
        void testAddCategoryWithNullNameThrowsException() {
            Category categoryWithNullName = new Category(null, "Valid Description");
            Exception exception = assertThrows(InvalidCategoryException.class, () -> categoryService.addCategory(categoryWithNullName));
            assertEquals("Name must be not null.", exception.getMessage());
        }

        @Test
        void testAddCategoryWithNullDescriptionThrowsException() {
            Category categoryWithNullDescription = new Category("Valid Name", null);
            Exception exception = assertThrows(InvalidCategoryException.class, () -> categoryService.addCategory(categoryWithNullDescription));
            assertEquals("Description cannot be null.", exception.getMessage());
        }


        @Test
        void testGetAllCategoriesWithError() {
            when(categoryRepository.findAll()).thenThrow(new RuntimeException("Database error"));
            assertThrows(RuntimeException.class, () -> categoryService.getAllCategories());
        }

        @Test
        void testUpdateCategoryWithInvalidData() {
            Category invalidCategory = new Category("originalName", null);
            assertThrows(InvalidCategoryException.class, () -> categoryService.updateCategory(invalidCategory));
        }

        @Test
        void testDeleteNonExistentCategory() {
            Long categoryId = 1L;
            when(categoryRepository.findById(categoryId)).thenReturn(null);
            assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(categoryId));
            verify(categoryRepository, never()).delete(any(Category.class));
        }

        @Test
        void testDeleteCategoryWithDependencies() {
            Category category = new Category("name", "description");
            when(categoryRepository.findById(category.getId())).thenReturn(category);
            doThrow(IllegalStateException.class).when(categoryRepository).delete(category);
            Long id = category.getId();
            Exception ex = assertThrows(InvalidCategoryException.class, () -> categoryService.deleteCategory(id));
            assertEquals("Cannot delete category with existing expenses", ex.getMessage());
        }
    }
}