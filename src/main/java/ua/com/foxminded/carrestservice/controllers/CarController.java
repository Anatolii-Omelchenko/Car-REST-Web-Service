package ua.com.foxminded.carrestservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.carrestservice.dto.CarDTO;
import ua.com.foxminded.carrestservice.models.Car;
import ua.com.foxminded.carrestservice.services.CarService;
import ua.com.foxminded.carrestservice.utils.DTOconverters.CarDTOConverter;
import ua.com.foxminded.carrestservice.utils.specifications.CarSpecification;
import ua.com.foxminded.carrestservice.utils.specifications.SortCriteria;
import ua.com.foxminded.carrestservice.utils.exceptions.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ua.com.foxminded.carrestservice.utils.ControllerUtils.handleBindingErrors;
import static ua.com.foxminded.carrestservice.utils.DTOconverters.CarDTOConverter.*;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;
    private final int PAGE_SIZE = 3;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Get all cars")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all cars",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CarDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Cars not found",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping
    public List<CarDTO> getCars(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "brand") String filter,
                                @RequestParam(defaultValue = "ASC") String sortDirection) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        SortCriteria sortCriteria = new SortCriteria(filter, sortDirection);
        CarSpecification carSpec = new CarSpecification(sortCriteria);
        List<Car> cars = carService.findAll(carSpec, pageable);

        if (cars.isEmpty()) {
            throw new CarNotFoundException("Cars was not found!");
        }

        return cars.stream().map(CarDTOConverter::convertToDTO).collect(Collectors.toList());
    }

    @Operation(summary = "Get car by its number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the car",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Cars not found",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping("/{number}")
    public CarDTO getCarByNumber(@PathVariable("number") String number) {
        return convertToDTO(carService.findByNumber(number));
    }

    @Operation(summary = "Delete car by number", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the car",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Car was not deleted",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json"))})
    @DeleteMapping("/{number}")
    public ResponseEntity<?> deleteCarByNumber(@PathVariable("number") String number) {
        if (carService.deleteByNumber(number) == 1) {
            return ResponseEntity.noContent().build();
        }
        String message = "Car with number " + number + " not found.";
        return ResponseEntity.badRequest().body(message);
    }

    @Operation(summary = "Add new car", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Add the car",
                    headers = @Header(name = "Location", description = "URI of the created car"),
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json"))})
    @PostMapping
    public ResponseEntity<?> registerCar(@RequestBody @Valid CarDTO carDTO, BindingResult bindingResult) {
        handleBindingErrors(bindingResult);
        Car car = convertFromDTO(carDTO);

        try {
            carService.save(car);
        } catch (DataIntegrityViolationException e) {
            throw new CarDataException("Car with number " + carDTO.getNumber() + " already exists!");
        }

        return ResponseEntity.created(URI.create("/cars/" + car.getNumber())).build();
    }

    @Operation(summary = "Update car number", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the car",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Cars not found",
                    content = @Content(mediaType = "application/json"))})
    @PutMapping("/{number}")
    public ResponseEntity<?> updateCar(@RequestBody @Valid CarDTO carDTO, BindingResult bindingResult, @PathVariable("number") String number) {
        if (bindingResult.hasFieldErrors("number")) {
            String errorMsg = bindingResult.getFieldError("number").getDefaultMessage();
            throw new CarDataException(errorMsg);
        }

        try {
            carService.updateNumber(number, carDTO.getNumber());
        } catch (DataIntegrityViolationException e) {
            throw new CarDataException("Car with number " + carDTO.getNumber() + " already exists!");
        }

        return ResponseEntity.ok(carDTO);
    }
}