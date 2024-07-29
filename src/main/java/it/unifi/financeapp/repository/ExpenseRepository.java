package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Expense;

public interface ExpenseRepository {

    Expense save (Expense expense);

    Expense findById(Long id);
}
