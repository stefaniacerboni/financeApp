package it.unifi.financeapp.model;

public class User {
    private String username;
    private String name;
    private String surname;
    private String email;

    public User(String username, String name, String surname, String email) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
