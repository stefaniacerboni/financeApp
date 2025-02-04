package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
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
    void testFindById() {
        Category newCategory = new Category("Utilities", "Utility bills");
        em.getTransaction().begin();
        em.persist(newCategory);
        em.getTransaction().commit();

        Category foundCategory = categoryRepository.findById(newCategory.getId());
        assertNotNull(foundCategory);
        assertEquals("Utilities", foundCategory.getName());
        assertEquals("Utility bills", foundCategory.getDescription());

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
        assertEquals(cat1, categories.get(0));
        assertEquals(cat2, categories.get(1));
    }

    private Category addCategory() {
        Category newCategory = new Category("New", "New Category Description");
        return categoryRepository.save(newCategory);
    }

    @Test
    void testSaveNewCategory() {
        Category newCategory = addCategory();
        assertNotNull(newCategory);
        em.clear();
        Category retrieved = em.find(Category.class, newCategory.getId());
        assertNotNull(retrieved);
        assertEquals("New", retrieved.getName());
        assertEquals("New Category Description", retrieved.getDescription());
    }

    @Test
    void testSaveExistingCategory() {
        Category existingCategory = new Category("Existing", "Existing Category Description");
        em.getTransaction().begin();
        em.persist(existingCategory);
        em.getTransaction().commit();

        existingCategory.setDescription("Updated Description");
        Category res = categoryRepository.save(existingCategory);
        assertNotNull(res);

        Category retrieved = em.find(Category.class, existingCategory.getId());
        assertNotNull(retrieved);
        assertEquals(existingCategory.getName(), retrieved.getName());
        assertEquals("Updated Description", retrieved.getDescription());
    }

    @Test
    void testUpdateCategory() {
        Category category = new Category("Existing", "Existing Category Description");
        em.getTransaction().begin();
        em.persist(category);
        em.getTransaction().commit();

        category.setName("Updated Name");
        Category res = categoryRepository.update(category);
        assertNotNull(res);

        em.clear();

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

        em.clear();

        Category retrieved = em.find(Category.class, category.getId());
        assertNull(retrieved);
    }

    @Test
    void testDeleteCategoryWithDependencies() {
        Category category = new Category("To Be Deleted", "To be deleted description");
        User user = new User("username", "email");
        em.getTransaction().begin();
        em.persist(category);
        em.persist(user);
        em.persist(new Expense(category, user, 0.0, "2024-12-12"));
        em.getTransaction().commit();
        assertThrows(IllegalStateException.class, () -> categoryRepository.delete(category));
        Category retrieved = em.find(Category.class, category.getId());
        assertNotNull(retrieved);
    }

    @Test
    void testDeleteAllCategories() {
        addCategory();
        categoryRepository.deleteAll();
        em.clear();
        // Use a new EntityManager for verification
        EntityManager emVerification = emf.createEntityManager();
        CategoryRepository categoryRepositoryVerification = new CategoryRepositoryImpl(emVerification);

        List<Category> categories = categoryRepositoryVerification.findAll();
        assertTrue(categories.isEmpty());

        emVerification.close();
    }
}
