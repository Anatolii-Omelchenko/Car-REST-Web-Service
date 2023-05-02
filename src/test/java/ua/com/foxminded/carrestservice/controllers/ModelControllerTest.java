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
import ua.com.foxminded.carrestservice.dto.ModelDTO;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.models.Model;
import ua.com.foxminded.carrestservice.security.SecurityConfig;
import ua.com.foxminded.carrestservice.services.BrandService;
import ua.com.foxminded.carrestservice.services.CategoryService;
import ua.com.foxminded.carrestservice.services.ModelService;
import ua.com.foxminded.carrestservice.utils.DTOconverters.ModelDTOConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModelController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(SecurityConfig.class)
public class ModelControllerTest {
    @MockBean
    private ModelService modelService;
    @MockBean
    private BrandService brandService;
    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;
    private Model model1;
    private Model model2;

    @BeforeAll
    public void setUp() {
        ModelDTOConverter.setServices(modelService, brandService, categoryService);
        model1 = new Model("Test_model_One", new Brand("Test_Brand"), 1991);
        model2 = new Model("Test_model_Two", new Brand("Test_Brand"), 1992);
    }

    @Test
    public void whenGetModelsShouldReturnListOfAllDTOModels() throws Exception {
        List<Model> models = Arrays.asList(model1, model2);
        when(modelService.findAll(any(), any())).thenReturn(models);

        mockMvc.perform(get("/api/v1/models"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].modelName", is(model1.getName())));
    }

    @Test
    public void whenGetOneModelShouldReturnOneDTOModel() throws Exception {
        String brand = model1.getBrand().getName();
        String model = model1.getName();
        int year = model1.getProductionYear();

        when(modelService.findByNameAndProductionYearAndBrandName(model, year, brand))
                .thenReturn(Optional.ofNullable(model1));

        mockMvc.perform(get("/api/v1/models/{brand}/{model}/{year}",
                        brand, model, year))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.modelName", is(model)));
    }

    @Test
    @WithMockUser
    public void whenDeleteModelWithValidParamsShouldDeleteModelFromDbAndReturnNoContentStatus() throws Exception {
        String brand = model1.getBrand().getName();
        String model = model1.getName();
        int year = model1.getProductionYear();

        when(modelService.delete(brand, year, model))
                .thenReturn(1);

        mockMvc.perform(delete("/api/v1/models/{brand}/{model}/{year}",
                        brand, model, year))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser
    public void whenDeleteModelWithInvalidParamsShouldReturnBadRequest() throws Exception {
        String brand = model1.getBrand().getName();
        String model = model1.getName();
        int year = model1.getProductionYear();

        when(modelService.delete(brand, year, model))
                .thenReturn(0);

        mockMvc.perform(delete("/api/v1/models/{brand}/{model}/{year}",
                        brand, model, year))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Model was not found."));
    }

    @Test
    @WithMockUser
    public void whenPutUpdateModelShouldUpdateModelInDbAndReturnOk() throws Exception {
        String brand = model1.getBrand().getName();
        String model = model1.getName();
        int year = model1.getProductionYear();
        ModelDTO modelDTO = new ModelDTO(brand, "New model", year, Set.of());

        when(modelService.findByNameAndProductionYearAndBrandName(model, year, brand)).thenReturn(Optional.ofNullable(model1));
        when(brandService.findByName(brand)).thenReturn(Optional.of(new Brand(brand)));

        mockMvc.perform(put("/api/v1/models/{brand}/{model}/{year}",
                        brand, model, year)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(modelDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(modelDTO.getModelName())));

        verify(modelService).update(any(Model.class), any(Model.class));
    }

    @Test
    @WithMockUser
    public void whenPostAddModelShouldUpdateModelInDbAndReturnCreatedStatus() throws Exception {
        String brand = model1.getBrand().getName();
        String model = model1.getName();
        int year = model1.getProductionYear();
        ModelDTO modelDTO = new ModelDTO(brand, "New_model", year, Set.of());

        when(modelService.findByNameAndProductionYearAndBrandName(model, year, brand)).thenReturn(Optional.ofNullable(model1));
        when(brandService.findByName(brand)).thenReturn(Optional.of(new Brand(brand)));

        mockMvc.perform(post("/api/v1/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(modelDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("/models/%s/%s/%d", brand, modelDTO.getModelName(), year)));

        Model savedModel = ModelDTOConverter.convertFromDTO(modelDTO);
        verify(modelService).save(savedModel);
    }
}