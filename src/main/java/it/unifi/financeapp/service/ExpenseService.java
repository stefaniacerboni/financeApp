package it.unifi.financeapp.service;

import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.repository.ExpenseRepository;
import it.unifi.financeapp.service.exceptions.InvalidExpenseException;
import org.hibernate.service.spi.ServiceException;

import javax.persistence.PersistenceException;

public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense addExpense(Expense expense) {
        validateExpense(expense);

        try {
            return expenseRepository.save(expense);
        } catch (PersistenceException pe) {
            throw new ServiceException("Error while adding expense", pe);
        }
    }

    private void validateExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Cannot add a null expense");
        }
        if (expense.getCategory() == null) {
            throw new InvalidExpenseException("Category cannot be null.");
        }
        if (expense.getAmount() <= 0) {
            throw new InvalidExpenseException("Amount must be greater than 0.");
        }
        if (expense.getDate() == null || expense.getDate().isEmpty()) {
            throw new InvalidExpenseException("Date cannot be empty.");
        }
    }
}
