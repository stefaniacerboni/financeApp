package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Expense;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

public class ExpenseRepositoryImpl implements ExpenseRepository {
    private final EntityManager entityManager;

    public ExpenseRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public Expense save(Expense expense) {
        entityManager.getTransaction().begin();
        manageDependencies(expense);
        if (expense.getId() == null) {
            entityManager.persist(expense);
        } else {
            expense = entityManager.merge(expense);
        }
        entityManager.getTransaction().commit();
        return expense;
    }

    @Override
    public Expense findById(Long id) {
        return entityManager.find(Expense.class, id);
    }

    @Transactional
    @Override
    public Expense update(Expense expense) {
        entityManager.getTransaction().begin();
        manageDependencies(expense);
        Expense result = entityManager.merge(expense);
        entityManager.getTransaction().commit();
        return result;
    }

    @Transactional
    @Override
    public void delete(Expense expense) {
        entityManager.getTransaction().begin();
        entityManager.remove(expense);
        entityManager.getTransaction().commit();
    }

    @Override
    public List<Expense> findAll() {
        return entityManager.createQuery("SELECT e FROM Expense e", Expense.class).getResultList();
    }

    @Transactional
    @Override
    public void deleteAll() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Expense").executeUpdate();
        entityManager.getTransaction().commit();
    }

    void manageDependencies(Expense expense) {
        expense.setUser(entityManager.merge(expense.getUser()));
        expense.setCategory(entityManager.merge(expense.getCategory()));
    }
}