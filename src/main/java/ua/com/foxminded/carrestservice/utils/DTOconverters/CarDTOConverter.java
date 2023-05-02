package ua.com.foxminded.carrestservice.utils.DTOconverters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.foxminded.carrestservice.dto.CarDTO;
import ua.com.foxminded.carrestservice.models.Car;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.models.Model;
import ua.com.foxminded.carrestservice.services.ModelService;
import ua.com.foxminded.carrestservice.utils.exceptions.ModelNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CarDTOConverter {

    private static ModelService modelService;

    @Autowired
    public CarDTOConverter(ModelService modelService) {
        CarDTOConverter.modelService = modelService;
    }

    public static CarDTO convertToDTO(Car car) {
        CarDTO carDTO = new CarDTO();
        carDTO.setNumber(car.getNumber());
        carDTO.setBrandName(car.getModel().getBrand().getName());
        carDTO.setModelName(car.getModel().getName());
        carDTO.setProductionYear(car.getModel().getProductionYear());

        Set<String> categories = car.getModel().getCategories().stream().map(Category::getName).collect(Collectors.toSet());
        carDTO.setCategories(categories);

        return carDTO;
    }

    public static Car convertFromDTO(CarDTO carDTO) {
        Model model = modelService
                .findByNameAndProductionYearAndBrandName(carDTO.getModelName(), carDTO.getProductionYear(), carDTO.getBrandName())
                .orElseThrow(() -> new ModelNotFoundException("Model was not found."));

        Car car = new Car();
        car.setModel(model);
        car.setNumber(carDTO.getNumber());

        return car;
    }

    public static void setModelService(ModelService service) {
        CarDTOConverter.modelService = service;
    }
}