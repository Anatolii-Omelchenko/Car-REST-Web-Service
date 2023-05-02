package ua.com.foxminded.carrestservice.utils.specifications;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import ua.com.foxminded.carrestservice.models.Car;
import ua.com.foxminded.carrestservice.utils.exceptions.InvalidSortKeyException;

public class CarSpecification implements Specification<Car> {

    private final SortCriteria sortCriteria;

    public CarSpecification(SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    @Override
    public Predicate toPredicate(Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path<?> path = switch (sortCriteria.getKey()) {
            case "brand" -> root.get("model").get("brand").get("name");
            case "number" -> root.get(sortCriteria.getKey());
            case "model" -> root.get("model").get("name");
            case "year" -> root.get("model").get("productionYear");
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