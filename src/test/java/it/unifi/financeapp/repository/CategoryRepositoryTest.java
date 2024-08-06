package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CategoryRepositoryTest {

    @Mock
    EntityManager entityManager;
    CategoryRepositoryImpl categoryRepository;
    @Mock
    private TypedQuery<Category> typedQuery;
    @Mock
    private NativeQuery<Category> nativeQuery;
    @Mock
    private EntityTransaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(entityManager.getTransaction()).thenReturn(transaction);
        categoryRepository = new CategoryRepositoryImpl(entityManager);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Category mockCategory = new Category("Utilities", "Utility bills");
        when(entityManager.find(Category.class, id)).thenReturn(mockCategory);

        Category result = categoryRepository.findById(id);
        verify(entityManager).find(Category.class, id);
        assertEquals("Utilities", result.getName());
        assertEquals("Utility bills", result.getDescription());
    }

    @Test
    void testFindAll() {
        Category cat1 = new Category("Utilities", "Utility bills");
        Category cat2 = new Category("Groceries", "Weekly food supplies");
        when(entityManager.createQuery("SELECT c FROM Category c", Category.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(cat1, cat2));

        List<Category> categories = categoryRepository.findAll();

        assertNotNull(categories);
        assertEquals(2, categories.size());
        verify(entityManager).createQuery("SELECT c FROM Category c", Category.class);
    }

    @Test
    void testSaveNewCategory() {
        Category newCategory = new Category("New", "New Category Description");

        categoryRepository.save(newCategory);

        verify(entityManager).persist(newCategory);
        verify(transaction).begin();
        verify(transaction).commit();
    }

    @Test
    void testSaveExistingCategory() {
        Category existingCategory = new Category("Existing", "Existing Category Description");
        existingCategory.setId(1L);  // Simulate an existing category

        when(entityManager.merge(existingCategory)).thenReturn(existingCategory);

        Category updatedCategory = categoryRepository.save(existingCategory);

        verify(entityManager).merge(existingCategory);
        verify(entityManager, never()).persist(existingCategory);
        assertNotNull(updatedCategory);
        assertEquals(Long.valueOf(1), updatedCategory.getId());
    }

    @Test
    void testUpdateCategory() {
        Category category = new Category("Existing", "Existing Category Description");
        category.setId(1L);

        when(entityManager.merge(category)).thenReturn(category);

        Category updated = categoryRepository.update(category);

        verify(entityManager).merge(category);
        assertEquals("Existing", updated.getName());
    }

    @Test
    void testDeleteCategory() {
        Category category = new Category("To Be Deleted", "To be deleted description");
        category.setId(1L);

        categoryRepository.delete(category);

        verify(entityManager).remove(category);
    }

    @Test
    void testDeleteAllCategories() {
        when(entityManager.createNativeQuery("DELETE c FROM categories c")).thenReturn(nativeQuery);

        categoryRepository.deleteAll();

        verify(entityManager).createNativeQuery("DELETE c FROM categories c");
    }
}
