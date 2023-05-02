package ua.com.foxminded.carrestservice.services.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.carrestservice.models.Car;
import ua.com.foxminded.carrestservice.models.Model;
import ua.com.foxminded.carrestservice.repositories.CarRepository;
import ua.com.foxminded.carrestservice.services.CarService;
import ua.com.foxminded.carrestservice.utils.exceptions.CarNotFoundException;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository repository;

    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public List<Car> findAll(Specification<Car> specification, Pageable pageable) {
        return repository.findAll(specification, pageable).getContent();
    }

    @Override
    @Transactional
    public Car findByNumber(String number) {
        return repository.findByNumber(number).orElseThrow(() -> new CarNotFoundException("Car with number '" + number + "' was not found"));
    }

    @Override
    @Transactional
    public List<Car> findByModel(Model model, Pageable pageable) {
        return repository.findByModel(model, pageable);
    }

    @Override
    @Transactional
    public void save(Car car) {
        repository.save(car);
    }

    @Override
    @Transactional
    public Integer deleteByNumber(String number) {
        return repository.deleteByNumber(number);
    }

    @Override
    @Transactional
    public void updateNumber(String oldNumber, String newNumber) {
        Car car = repository.findByNumber(oldNumber)
                .orElseThrow(() -> new CarNotFoundException("Car with number '" + oldNumber + "' was not found"));
        car.setNumber(newNumber);
        repository.save(car);
    }
}