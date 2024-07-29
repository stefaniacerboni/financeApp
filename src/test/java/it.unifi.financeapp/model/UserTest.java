package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserTest {

    @Test
    void testCreateUser(){
        String username = "Username";
        String name = "Name";
        String surname = "Surname";
        String email = "Email";
        User user = new User(username, name, surname, email);
        assertEquals(username, user.getUsername());
        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals(email, user.getEmail());
    }

    @Test
    void testSetUserDetails(){
        String username = "Username";
        String name = "Name";
        String surname = "Surname";
        String email = "Email";
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        assertEquals(username, user.getUsername());
        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals(email, user.getEmail());
    }

    @Test
    void testCreateUserWithNoNameSurname(){
        String username = "Username";
        String email = "Email";
        User user = new User(username, email);
        assertEquals(username, user.getUsername());
        assertNull(user.getName());
        assertNull(user.getSurname());
        assertEquals(email, user.getEmail());
    }
}
