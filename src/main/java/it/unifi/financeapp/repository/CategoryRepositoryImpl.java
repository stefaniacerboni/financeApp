package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;

import javax.persistence.EntityManager;
import java.util.List;

public class CategoryRepositoryImpl implements CategoryRepository {

    private final EntityManager entityManager;

    public CategoryRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Category findById(Long id) {
        return entityManager.find(Category.class, id);
    }

    @Override
    public List<Category> findAll() {
        return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    @Override
    public Category save(Category category) {
        entityManager.getTransaction().begin();
        if (category.getId() == null) {
            entityManager.persist(category);
        } else {
            category = entityManager.merge(category);
        }
        entityManager.getTransaction().commit();
        return category;
    }

    @Override
    public Category update(Category category) {
        entityManager.getTransaction().begin();
        Category result = entityManager.merge(category);
        entityManager.getTransaction().commit();
        return result;
    }

    @Override
    public void delete(Category category) {
        // Check if any expenses reference this category
        Long expenseCount = entityManager.createQuery(
                        "SELECT COUNT(e) FROM Expense e WHERE e.category = :category", Long.class)
                .setParameter("category", category)
                .getSingleResult();

        if (expenseCount > 0) {
            System.err.println("Cannot delete category with existing expenses.");
            throw new IllegalStateException("Cannot delete category with existing expenses.");
        }
        entityManager.getTransaction().begin();
        entityManager.remove(category);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteAll() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE c FROM categories c").executeUpdate();
        entityManager.getTransaction().commit();
    }
}
