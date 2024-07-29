package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User update(User user);

}
