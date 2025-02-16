package it.unifi.financeapp.service;

import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.repository.ExpenseRepository;
import it.unifi.financeapp.service.exceptions.InvalidExpenseException;
import org.hibernate.service.spi.ServiceException;

import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ExpenseService {

	private final ExpenseRepository expenseRepository;
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

	public Expense findExpenseById(Long id) {
		return expenseRepository.findById(id);
	}

	public Expense updateExpense(Expense expense) {
		validateExpense(expense);
		return expenseRepository.update(expense);
	}

	public void deleteExpense(Long id) {
		Expense expense = expenseRepository.findById(id);
		if (expense != null)
			expenseRepository.delete(expense);
		else
			throw new IllegalArgumentException("Cannot delete a null expense.");

	}

	public List<Expense> getAllExpenses() {
		return expenseRepository.findAll();
	}

	public void deleteAll() {
		expenseRepository.deleteAll();
	}

	private void validateExpense(Expense expense) {
		if (expense == null) {
			throw new IllegalArgumentException("Cannot add a null expense");
		}
		if (expense.getCategory() == null) {
			throw new InvalidExpenseException("Category cannot be null.");
		}
		if (expense.getUser() == null) {
			throw new InvalidExpenseException("User cannot be null.");
		}
		if (expense.getAmount() <= 0) {
			throw new InvalidExpenseException("Amount must be greater than 0.");
		}
		if (!isValidDate(expense.getDate())) {
			throw new InvalidExpenseException("Date is invalid.");
		}
	}

	boolean isValidDate(String date) {
		if (date == null || date.isEmpty()) {
			return false;
		}
		try {
			LocalDate.parse(date, DATE_FORMATTER);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
}