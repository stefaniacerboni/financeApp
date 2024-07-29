package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User update(User user) {
        entityManager.getTransaction().begin();
        User result = entityManager.merge(user);
        entityManager.getTransaction().commit();
        return result;
    }

    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void delete(User user) {
        entityManager.getTransaction().begin();
        entityManager.remove(user);
        entityManager.getTransaction().commit();
    }

    @Transactional
    @Override
    public void deleteAll() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE u FROM users u").executeUpdate();
        entityManager.getTransaction().commit();
    }
}
