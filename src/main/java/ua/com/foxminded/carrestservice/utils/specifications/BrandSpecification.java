package ua.com.foxminded.carrestservice.utils.specifications;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import ua.com.foxminded.carrestservice.models.Brand;

public class BrandSpecification implements Specification<Brand> {
    private final SortCriteria sortCriteria;

    public BrandSpecification(SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    @Override
    public Predicate toPredicate(Root<Brand> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path<?> path = root.get("name");
        if (sortCriteria.getDirection().equalsIgnoreCase("DESC")) {
            query.orderBy(builder.desc(path));
        } else {
            query.orderBy(builder.asc(path));
        }

        return null;
    }
}
