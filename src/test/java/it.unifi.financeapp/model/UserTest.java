package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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


    @Test
    void testEqualsWithSelf() {
        User user = new User("Username", "Name", "Surname", "Email");
        assertEquals(user, user);
    }

    @Test
    void testEqualsWithSameData() {
        User user1 = new User("Username", "Name", "Surname", "Email");
        User user2 = new User("Username", "Name", "Surname", "Email");
        assertEquals(user1, user2);
    }

    @Test
    void testEqualsWithDifferentData() {
        User user1 = new User("Username", "Name", "Surname", "Email");
        User user2 = new User("Username2", "Name2", "Surname2", "Email2");
        assertNotEquals(user1, user2);
    }

    @Test
    void testEqualsAgainstNull() {
        User user = new User("Username", "Name", "Surname", "Email");
        assertNotEquals(user, null);
    }

    @Test
    void testEqualsAgainstDifferentClass() {
        User user = new User("Username", "Name", "Surname", "Email");
        Object other = new Object();
        assertNotEquals(user, other);
    }

    @Test
    void testHashCodeConsistency() {
        User user = new User("Username", "Name", "Surname", "Email");
        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testEqualObjectsSameHashCode() {
        User user1 = new User("Username", "Name", "Surname", "Email");
        User user2 = new User("Username", "Name", "Surname", "Email");
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
