package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Expense;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

public class ExpenseRepositoryImpl implements ExpenseRepository{
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

    private void manageDependencies(Expense expense) {
        if (expense.getUser() != null && !entityManager.contains(expense.getUser())) {
            expense.setUser(entityManager.merge(expense.getUser()));
        }
        if (expense.getCategory() != null && !entityManager.contains(expense.getCategory())) {
            expense.setCategory(entityManager.merge(expense.getCategory()));
        }
    }
}
