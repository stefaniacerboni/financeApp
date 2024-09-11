package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void init() {
        emf = Persistence.createEntityManagerFactory("TestFinanceAppH2PU");
        em = emf.createEntityManager();
        categoryRepository = new CategoryRepositoryImpl(em);
    }

    @AfterEach
    public void close() {
        em.close();
        emf.close();
    }

    @Test
    public void testFindById() {
        Category newCategory = new Category("Utilities", "Utility bills");
        em.getTransaction().begin();
        em.persist(newCategory);
        em.getTransaction().commit();

        Category foundCategory = categoryRepository.findById(newCategory.getId());
        assertNotNull(foundCategory);
        assertEquals("Utilities", foundCategory.getName());
    }

    @Test
    void testFindAll() {
        em.getTransaction().begin();
        Category cat1 = new Category("Utilities", "Utility bills");
        Category cat2 = new Category("Groceries", "Weekly food supplies");
        em.persist(cat1);
        em.persist(cat2);
        em.getTransaction().commit();

        List<Category> categories = categoryRepository.findAll();

        assertNotNull(categories);
        assertEquals(2, categories.size());
    }

    @Test
    void testSaveNewCategory() {
        Category newCategory = new Category("New", "New Category Description");

        categoryRepository.save(newCategory);

        Category retrieved = em.find(Category.class, newCategory.getId());
        assertNotNull(retrieved);
        assertEquals("New", retrieved.getName());
    }

    @Test
    void testSaveExistingCategory() {
        Category existingCategory = new Category("Existing", "Existing Category Description");
        em.getTransaction().begin();
        em.persist(existingCategory);
        em.getTransaction().commit();

        existingCategory.setDescription("Updated Description");
        Category updatedCategory = categoryRepository.save(existingCategory);

        Category retrieved = em.find(Category.class, existingCategory.getId());
        assertNotNull(retrieved);
        assertEquals("Updated Description", retrieved.getDescription());
    }

    @Test
    void testUpdateCategory() {
        Category category = new Category("Existing", "Existing Category Description");
        em.getTransaction().begin();
        em.persist(category);
        em.getTransaction().commit();

        category.setName("Updated Name");
        Category updated = categoryRepository.update(category);

        Category retrieved = em.find(Category.class, category.getId());
        assertEquals("Updated Name", retrieved.getName());
    }

    @Test
    void testDeleteCategory() {
        Category category = new Category("To Be Deleted", "To be deleted description");
        em.getTransaction().begin();
        em.persist(category);
        em.getTransaction().commit();

        categoryRepository.delete(category);

        Category retrieved = em.find(Category.class, category.getId());
        assertNull(retrieved);
    }

    @Test
    void testDeleteAllCategories() {
        testSaveNewCategory();
        categoryRepository.deleteAll();

        List<Category> categories = categoryRepository.findAll();
        assertTrue(categories.isEmpty());
    }
}
