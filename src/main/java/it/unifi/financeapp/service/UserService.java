package it.unifi.financeapp.service;

import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.service.exceptions.InvalidUserException;
import org.hibernate.service.spi.ServiceException;

import javax.persistence.PersistenceException;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        validateUser(user);

        try {
            return userRepository.save(user);
        } catch (PersistenceException pe) {
            throw new ServiceException("Error while adding user", pe);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot add a null user.");
        }
        if (user.getUsername() == null) {
            throw new InvalidUserException("Username must be not null.");
        }
        if (user.getUsername().trim().length() == 0) {
            throw new InvalidUserException("Username must be not null.");
        }
        if (user.getEmail() == null) {
            throw new InvalidUserException("Email cannot be null.");
        }
    }
}
