package it.unifi.financeapp.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "FK_expense_category"))
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_expense_user"))
    private User user;
    private double amount;
    private String date;

    public Expense(Category category, User user, double amount, String date) {
        this.category = category;
        this.user = user;
        this.amount = amount;
        this.date = date;
    }

    public Expense() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
