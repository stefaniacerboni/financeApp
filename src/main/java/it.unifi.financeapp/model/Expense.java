package it.unifi.financeapp.model;

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
}
