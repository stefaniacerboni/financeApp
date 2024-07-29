package it.unifi.financeapp.service;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;
    @Nested
    @DisplayName("Happy Cases")
    class HappyCases {
        @Test
        void testAddCategory() {
            Category category = new Category("name", "description");
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            Category result = categoryService.addCategory(category);
            assertNotNull(result);
            Assertions.assertEquals(category.getName(), result.getName());
            Assertions.assertEquals(category.getDescription(), result.getDescription());
            verify(categoryRepository).save(category);
        }
    }
}
