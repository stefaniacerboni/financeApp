package it.unifi.financeapp.model;

public class Category {
    private String name;
    private String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category() {

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
