package ua.com.foxminded.carrestservice.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.utils.specifications.CategorySpecification;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    @Transactional
    List<Category> findAll();

    @Transactional
    Optional<Category> findByName(String name);

    @Transactional
    List<Category> findAll(CategorySpecification spec, Pageable pageable);

    @Transactional
    Integer delete(String name);

    @Transactional
    void save(Category category);

    @Transactional
    void update(Category oldCategory, Category newCategory);
}