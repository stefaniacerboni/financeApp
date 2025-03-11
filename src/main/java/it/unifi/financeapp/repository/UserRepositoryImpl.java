package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

	private final EntityManager entityManager;

	public UserRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public User save(User user) {
		entityManager.getTransaction().begin();
		try {
			if (user.getId() == null) {
				entityManager.persist(user);
			} else {
				user = entityManager.merge(user);
			}
		} catch (PersistenceException pe) {
			entityManager.getTransaction().rollback();
			throw pe;
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
		User res;
		entityManager.getTransaction().begin();
		try {
			res = entityManager.merge(user);
			entityManager.flush();
		} catch (PersistenceException pe) {
			entityManager.getTransaction().rollback();
			throw pe;
		}
		entityManager.getTransaction().commit();
		return res;
	}

	@Override
	public User findById(Long id) {
		return entityManager.find(User.class, id);
	}

	@Override
	public void delete(User user) {
		// Check if any expenses reference this user
		Long expenseCount = entityManager.createQuery("SELECT COUNT(e) FROM Expense e WHERE e.user = :user", Long.class)
				.setParameter("user", user).getSingleResult();

		if (expenseCount > 0) {
			throw new IllegalStateException("Cannot delete user with existing expenses.");
		}
		entityManager.getTransaction().begin();
		entityManager.remove(user);
		entityManager.getTransaction().commit();
	}

	@Transactional
	@Override
	public void deleteAll() {
		entityManager.getTransaction().begin();
		entityManager.createQuery("DELETE FROM User").executeUpdate();
		entityManager.getTransaction().commit();
	}
}