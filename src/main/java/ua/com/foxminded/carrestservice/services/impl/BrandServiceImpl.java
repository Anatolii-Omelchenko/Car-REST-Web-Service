package ua.com.foxminded.carrestservice.services.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.repositories.BrandRepository;
import ua.com.foxminded.carrestservice.services.BrandService;
import ua.com.foxminded.carrestservice.utils.specifications.BrandSpecification;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {
    private final BrandRepository repository;

    public BrandServiceImpl(BrandRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public List<Brand> findAll(BrandSpecification spec, Pageable pageable) {
        return repository.findAll(spec, pageable).getContent();
    }

    @Override
    @Transactional
    public Optional<Brand> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    @Transactional
    public Integer deleteByName(String brandName) {
        return repository.deleteByName(brandName);
    }

    @Override
    @Transactional
    public void add(Brand brand) {
        repository.save(brand);
    }

    @Override
    @Transactional
    public void update(Brand oldBrand, Brand newBrand) {
        oldBrand.setName(newBrand.getName());
        repository.save(oldBrand);
    }
}