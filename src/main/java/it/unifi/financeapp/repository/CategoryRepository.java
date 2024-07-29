package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;

import java.util.List;

public interface CategoryRepository {
    Category findById(Long id);

    List<Category> findAll();

    Category save(Category category);

    Category update(Category category);

    void delete(Category category);

    void deleteAll();
}
