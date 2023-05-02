package ua.com.foxminded.carrestservice.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.carrestservice.models.Car;
import ua.com.foxminded.carrestservice.models.Model;

import java.util.List;

public interface CarService {
    @Transactional
    List<Car> findAll(Specification<Car> specification, Pageable pageable);

    @Transactional
    Car findByNumber(String number);

    @Transactional
    List<Car> findByModel(Model model, Pageable pageable);

    @Transactional
    void save(Car car);

    @Transactional
    Integer deleteByNumber(String number);

    @Transactional
    void updateNumber(String oldNumber, String newNumber);
}