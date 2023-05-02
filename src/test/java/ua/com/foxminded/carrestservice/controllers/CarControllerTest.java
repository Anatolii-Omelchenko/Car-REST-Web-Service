package ua.com.foxminded.carrestservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.carrestservice.dto.CarDTO;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.models.Car;
import ua.com.foxminded.carrestservice.models.Model;
import ua.com.foxminded.carrestservice.security.SecurityConfig;
import ua.com.foxminded.carrestservice.services.CarService;
import ua.com.foxminded.carrestservice.services.ModelService;
import ua.com.foxminded.carrestservice.utils.DTOconverters.CarDTOConverter;
import ua.com.foxminded.carrestservice.utils.exceptions.CarDataException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(SecurityConfig.class)
public class CarControllerTest {

    @MockBean
    private CarService carService;

    @MockBean
    private ModelService modelService;

    @Autowired
    private MockMvc mockMvc;

    private Car car1;
    private Car car2;

    @BeforeAll
    public void setUp() {
        CarDTOConverter.setModelService(modelService);
        car1 = new Car(1L, "UN-1234", new Model("X-2", new Brand("Brand-I"), 1991));
        car2 = new Car(2L, "UN-4422", new Model("X-110", new Brand("Brand-I"), 1994));
    }

    @Test
    public void whenGetCarsShouldReturnListOfAllDTOCars() throws Exception {
        List<Car> cars = List.of(car1, car2);
        when(carService.findAll(any(), any())).thenReturn(cars);

        mockMvc.perform(get("/api/v1/cars"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].number", is(car1.getNumber())));
    }

    @Test
    public void whenGetCarByNumberShouldReturnCarDTO() throws Exception {
        String number = car1.getNumber();
        when(carService.findByNumber(number)).thenReturn(car1);
        mockMvc.perform(get("/api/v1/cars/{number}", number))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number", is(number)));
    }

    @Test
    @WithMockUser
    public void whenDeleteCarByNumberWithValidNumberShouldDeleteCarFromDbAndReturnNoContentStatus() throws Exception {
        String number = car1.getNumber();
        when(carService.deleteByNumber(number)).thenReturn(1);
        mockMvc.perform(delete("/api/v1/cars/{number}", number))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser
    public void whenDeleteCarByNumberWithInvalidNumberShouldReturnBadRequest() throws Exception {
        String number = car1.getNumber();
        when(carService.deleteByNumber(number)).thenReturn(0);
        mockMvc.perform(delete("/api/v1/cars/{number}", number))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Car with number " + number + " not found."));
    }

    @Test
    public void whenDeleteCarByNumberWithNoAuthorityUserShouldReturnUnauthorizedStatus() throws Exception {
        String number = car1.getNumber();
        when(carService.deleteByNumber(number)).thenReturn(1);
        mockMvc.perform(delete("/api/v1/cars/{number}", number))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void whenPostAddShouldSaveCarToDbAbdReturnCreatedStatus() throws Exception {
        String number = car1.getNumber();
        String brand = car1.getModel().getBrand().getName();
        String model = car1.getModel().getName();
        int year = car1.getModel().getProductionYear();
        CarDTO carDTO = new CarDTO(number, brand, model, year, Set.of());

        when(modelService.findByNameAndProductionYearAndBrandName(model, year, brand)).thenReturn(Optional.of(new Model()));
        when(carService.deleteByNumber(number)).thenReturn(0);

        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(carDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cars/" + number));

        Car savedCar = CarDTOConverter.convertFromDTO(carDTO);
        verify(carService).save(savedCar);
    }

    @Test
    @WithMockUser
    public void whenPutUpdateCarWithValidParamShouldUpdateCarInDbAndReturnOk() throws Exception {
        String number = car1.getNumber();
        String brand = car1.getModel().getBrand().getName();
        String model = car1.getModel().getName();
        int year = car1.getModel().getProductionYear();
        CarDTO carDTO = new CarDTO(number, brand, model, year, Set.of());

        when(carService.findByNumber(number)).thenReturn(car1);

        mockMvc.perform(put("/api/v1/cars/{number}", number)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(carDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number", is(carDTO.getNumber())));

        verify(carService).updateNumber(any(), any());
    }

    @Test
    @WithMockUser
    public void whenPutUpdateCarNumberThatAlreadyExistsShouldReturnBadRequest() throws Exception {
        String number = car1.getNumber();
        String brand = car1.getModel().getBrand().getName();
        String model = car1.getModel().getName();
        int year = car1.getModel().getProductionYear();
        CarDTO carDTO = new CarDTO(number, brand, model, year, Set.of());

        doThrow(new CarDataException("Car with number " + number + " already exists!"))
                .when(carService).updateNumber(number, number);

        mockMvc.perform(put("/api/v1/cars/{number}", number)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(carDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Car with number " + number + " already exists!")));

        verify(carService).updateNumber(any(), any());
    }
}