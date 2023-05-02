package ua.com.foxminded.carrestservice.utils.specifications;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.models.Category;

public class CategorySpecification implements Specification<Category> {
    private final SortCriteria sortCriteria;

    public CategorySpecification(SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    @Override
    public Predicate toPredicate(Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path<?> path = root.get("name");
        if (sortCriteria.getDirection().equalsIgnoreCase("DESC")) {
            query.orderBy(builder.desc(path));
        } else {
            query.orderBy(builder.asc(path));
        }

        return null;
    }
}
