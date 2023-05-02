package ua.com.foxminded.carrestservice.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.repositories.CategoryRepository;
import ua.com.foxminded.carrestservice.services.CategoryService;
import ua.com.foxminded.carrestservice.utils.specifications.CategorySpecification;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public List<Category> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Optional<Category> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    @Transactional
    public List<Category> findAll(CategorySpecification spec, Pageable pageable) {
        return repository.findAll(spec, pageable).getContent();
    }

    @Override
    @Transactional
    public Integer delete(String name) {
        return repository.deleteByName(name);
    }

    @Override
    @Transactional
    public void save(Category category) {
        repository.save(category);
    }

    @Override
    @Transactional
    public void update(Category oldCategory, Category newCategory) {
        oldCategory.setName(newCategory.getName());
        repository.save(oldCategory);
    }
}