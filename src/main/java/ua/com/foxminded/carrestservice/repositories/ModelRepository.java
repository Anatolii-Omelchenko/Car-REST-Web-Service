package ua.com.foxminded.carrestservice.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.models.Model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long>, JpaSpecificationExecutor<Model> {
    List<Model> findByBrand(Brand brand, Pageable pageable);

    List<Model> findByProductionYearBetween(Integer start, Integer end, Pageable pageable);

    List<Model> findByCategoriesIn(Set<Category> categories, Pageable pageable);

    Optional<Model> findByName(String name);

    Optional<Model> findByNameAndProductionYearAndBrandName(String name, Integer productionYear, String brandName);

    Integer deleteByBrandNameAndNameAndProductionYear(String brandName, String name, Integer productionYear);
}