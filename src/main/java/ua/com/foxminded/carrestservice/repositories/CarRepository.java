package ua.com.foxminded.carrestservice.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.carrestservice.models.Car;
import ua.com.foxminded.carrestservice.models.Model;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    Optional<Car> findByNumber(String number);

    List<Car> findByModel(Model model, Pageable pageable);

    Integer deleteByNumber(String number);
}