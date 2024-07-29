package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.User;

import javax.persistence.EntityManager;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private final EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User save(User user) {
        entityManager.getTransaction().begin();
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            user = entityManager.merge(user);
        }
        entityManager.getTransaction().commit();
        return user;
    }
}
