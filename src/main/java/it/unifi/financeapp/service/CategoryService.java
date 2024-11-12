package it.unifi.financeapp.service;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.service.exceptions.InvalidCategoryException;
import org.hibernate.service.spi.ServiceException;

import javax.persistence.PersistenceException;
import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category addCategory(Category category) {
        validateCategory(category);

        try {
            return categoryRepository.save(category);
        } catch (PersistenceException pe) {
            throw new ServiceException("Error while adding category", pe);
        }
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category updateCategory(Category category) {
        validateCategory(category);
        return categoryRepository.update(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id);
        if (category != null) {
            try {
                categoryRepository.delete(category);
            } catch (IllegalStateException e) {
                throw new InvalidCategoryException("Cannot delete category with existing expenses");
            }
        } else
            throw new IllegalArgumentException("Cannot delete a null expense.");

    }

    public void deleteAll() {
        categoryRepository.deleteAll();
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Cannot add a null category.");
        }
        if (category.getName() == null) {
            throw new InvalidCategoryException("Name must be not null.");
        }
        if (category.getName().trim().length() == 0) {
            throw new InvalidCategoryException("Name must be not null.");
        }
        if (category.getDescription() == null) {
            throw new InvalidCategoryException("Description cannot be null.");
        }
    }

}
