package ua.com.foxminded.carrestservice.services.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.models.Model;
import ua.com.foxminded.carrestservice.repositories.ModelRepository;
import ua.com.foxminded.carrestservice.services.ModelService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ModelServiceImpl implements ModelService {
    private final ModelRepository modelRepository;

    public ModelServiceImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    @Transactional
    public List<Model> findAll(Specification<Model> specification, Pageable pageable) {
        return modelRepository.findAll(specification, pageable).getContent();
    }

    @Override
    @Transactional
    public Optional<Model> findByName(String name) {
        return modelRepository.findByName(name);
    }

    @Override
    @Transactional
    public Optional<Model> findByNameAndProductionYearAndBrandName(String name, Integer productionYear, String brandName) {
        return modelRepository.findByNameAndProductionYearAndBrandName(name, productionYear, brandName);
    }

    @Override
    @Transactional
    public List<Model> findByBrand(Brand brand, Pageable pageable) {
        return modelRepository.findByBrand(brand, pageable);
    }

    @Override
    @Transactional
    public List<Model> findByProductionYear(Integer start, Integer end, Pageable pageable) {
        return modelRepository.findByProductionYearBetween(start, end, pageable);
    }

    @Override
    @Transactional
    public List<Model> findByCategories(Category category, Pageable pageable) {
        return modelRepository.findByCategoriesIn(Set.of(category), pageable);
    }

    @Override
    @Transactional
    public Integer delete(String brand, Integer year, String model) {
        return modelRepository.deleteByBrandNameAndNameAndProductionYear(brand, model, year);
    }

    @Override
    @Transactional
    public void save(Model model) {
        for (Category category : model.getCategories()) {
            category.addModel(model);
        }
        modelRepository.save(model);
    }

    @Override
    @Transactional
    public void update(Model oldModel, Model updatedModel) {

        for (Category category : oldModel.getCategories()) {
            category.removeModel(oldModel);
        }

        oldModel.setCategories(updatedModel.getCategories());
        oldModel.setBrand(updatedModel.getBrand());
        oldModel.setName(updatedModel.getName());
        oldModel.setProductionYear(updatedModel.getProductionYear());

        for (Category category : updatedModel.getCategories()) {
            category.addModel(oldModel);
        }

        modelRepository.save(oldModel);
    }
}