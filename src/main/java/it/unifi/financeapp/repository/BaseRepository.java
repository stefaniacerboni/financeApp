package it.unifi.financeapp.repository;

import java.util.List;

public interface BaseRepository<T> {

	T findById(Long id);

	List<T> findAll();

	T save(T category);

	T update(T category);

	void delete(T category);

	void deleteAll();
}
