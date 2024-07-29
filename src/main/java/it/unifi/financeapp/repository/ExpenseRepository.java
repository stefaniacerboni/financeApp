package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Expense;

import java.util.List;

public interface ExpenseRepository {

    Expense save (Expense expense);

    Expense findById(Long id);

    Expense update(Expense expense);

    void delete(Expense expense);

    List<Expense> findAll();

    void deleteAll();
}
