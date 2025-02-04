package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testCreateUser() {
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
    void testSetUserDetails() {
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
    void testCreateUserWithNoNameSurname() {
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
    void testEqualsWithDifferentUsername() {
        User user1 = new User("Username1", "Name", "Surname", "Email");
        User user2 = new User("Username2", "Name", "Surname", "Email");
        assertNotEquals(user1, user2, "Users should not be equal if usernames are different");
    }

    @Test
    void testEqualsWithDifferentName() {
        User user1 = new User("Username", "Name1", "Surname", "Email");
        User user2 = new User("Username", "Name2", "Surname", "Email");
        assertNotEquals(user1, user2, "Users should not be equal if names are different");
    }

    @Test
    void testEqualsWithDifferentSurname() {
        User user1 = new User("Username", "Name", "Surname1", "Email");
        User user2 = new User("Username", "Name", "Surname2", "Email");
        assertNotEquals(user1, user2, "Users should not be equal if surnames are different");
    }

    @Test
    void testEqualsWithDifferentEmail() {
        User user1 = new User("Username", "Name", "Surname", "Email1");
        User user2 = new User("Username", "Name", "Surname", "Email2");
        assertNotEquals(user1, user2, "Users should not be equal if emails are different");
    }

    @Test
    void testEqualsWithNullValues() {
        User user1 = new User(null, null, null, null);
        User user2 = new User(null, null, null, null);
        assertEquals(user1, user2, "Users should be equal if all fields are null");

        User user3 = new User("Username", "Name", "Surname", "Email");
        assertNotEquals(user1, user3, "Users should not be equal if one has all null fields and the other does not");
    }

    @Test
    void testEqualsWithSomeNullValues() {
        User user1 = new User("Username", null, "Surname", "Email");
        User user2 = new User("Username", "Name", "Surname", "Email");
        assertNotEquals(user1, user2, "Users should not be equal if one has null name");

        User user3 = new User("Username", "Name", null, "Email");
        User user4 = new User("Username", "Name", "Surname", "Email");
        assertNotEquals(user3, user4, "Users should not be equal if one has null surname");

        User user5 = new User("Username", "Name", "Surname", null);
        User user6 = new User("Username", "Name", "Surname", "Email");
        assertNotEquals(user5, user6, "Users should not be equal if one has null email");
    }

    @Test
    void testHashCodeImplementation() {
        User user = new User("Username", "Name", "Surname", "Email");
        int expectedHashCode = Objects.hash(user.getUsername(), user.getName(), user.getSurname(), user.getEmail());
        int actualHashCode = user.hashCode();
        assertEquals(expectedHashCode, actualHashCode,
                "The hashCode method should compute the hash based on the name and description fields");
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

    @Test
    void testToString() {
        User user = new User("Username", "Name", "Surname", "Email");
        String expected = "username='Username', email='Email'";
        assertEquals(expected, user.toString(), "The toString method should return the correct representation");
    }
}
