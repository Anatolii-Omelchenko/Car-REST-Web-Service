package ua.com.foxminded.carrestservice.services;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.utils.specifications.BrandSpecification;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    @Transactional
    List<Brand> findAll(BrandSpecification spec, Pageable pageable);

    @Transactional
    Optional<Brand> findByName(String name);

    @Transactional
    Integer deleteByName(String brandName);

    @Transactional
    void add(Brand brand);

    @Transactional
    void update(Brand oldBrand, Brand newBrand);
}