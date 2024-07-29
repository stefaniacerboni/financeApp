package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    void testCreateUser(){
        String username = "Username";
        String name = "Name";
        String surname = "Surname";
        String email = "Email";
        User user = new User(username, name, surname, email);
        assertEquals(name, user.getName());
    }
}
