package ua.com.foxminded.carrestservice.utils.specifications;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import ua.com.foxminded.carrestservice.models.Model;
import ua.com.foxminded.carrestservice.utils.exceptions.InvalidSortKeyException;

public class ModelSpecification implements Specification<Model> {

    private final SortCriteria sortCriteria;

    public ModelSpecification(SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    @Override
    public Predicate toPredicate(Root<Model> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path<?> path = switch (sortCriteria.getKey()) {
            case "brand" -> root.get("brand").get("name");
            case "model" -> root.get("name");
            case "year" -> root.get("productionYear");
            default -> throw new InvalidSortKeyException("Invalid sort key");
        };

        if (sortCriteria.getDirection().equalsIgnoreCase("DESC")) {
            query.orderBy(builder.desc(path));
        } else {
            query.orderBy(builder.asc(path));
        }

        return null;
    }
}