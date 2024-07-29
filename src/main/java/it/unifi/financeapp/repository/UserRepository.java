package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.User;

import java.util.List;

public interface UserRepository {

    User findById(Long id);

    List<User> findAll();

    User save(User user);

    User update(User user);

}
