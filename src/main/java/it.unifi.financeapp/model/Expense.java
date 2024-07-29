package it.unifi.financeapp.model;

import java.util.Objects;

public class Expense {
    private Category category;
    private User user;
    private Long amount;
    private String date;

    public Expense(Category category, User user, Long amount, String date) {
        this.category = category;
        this.user = user;
        this.amount = amount;
        this.date = date;
    }

    public Expense() {

    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(category, expense.category) && Objects.equals(user, expense.user) && Objects.equals(amount, expense.amount) && Objects.equals(date, expense.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, user, amount, date);
    }
}
