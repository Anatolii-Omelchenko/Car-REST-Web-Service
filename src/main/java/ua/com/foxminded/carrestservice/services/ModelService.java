package ua.com.foxminded.carrestservice.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.models.Model;

import java.util.List;
import java.util.Optional;

public interface ModelService {
    @Transactional
    List<Model> findAll(Specification<Model> specification, Pageable pageable);

    @Transactional
    Optional<Model> findByName(String name);

    @Transactional
    Optional<Model> findByNameAndProductionYearAndBrandName(String name, Integer productionYear, String brandName);

    @Transactional
    List<Model> findByBrand(Brand brand, Pageable pageable);

    @Transactional
    List<Model> findByProductionYear(Integer start, Integer end, Pageable pageable);

    @Transactional
    List<Model> findByCategories(Category category, Pageable pageable);

    @Transactional
    Integer delete(String brand, Integer year, String model);

    @Transactional
    void save(Model model);

    @Transactional
    void update(Model oldModel, Model updatedModel);
}