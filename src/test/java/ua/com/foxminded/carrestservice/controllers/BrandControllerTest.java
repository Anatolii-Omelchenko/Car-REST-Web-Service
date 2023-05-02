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
import ua.com.foxminded.carrestservice.dto.BrandDTO;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.security.SecurityConfig;
import ua.com.foxminded.carrestservice.services.BrandService;
import ua.com.foxminded.carrestservice.utils.DTOconverters.BrandDTOConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BrandController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(SecurityConfig.class)
public class BrandControllerTest {
    @MockBean
    private BrandService brandService;

    @Autowired
    private MockMvc mockMvc;

    private Brand brand1;
    private Brand brand2;

    @BeforeAll
    public void setUp() {
        BrandDTOConverter.setBrandService(brandService);
        brand1 = new Brand("brand_One");
        brand2 = new Brand("brand_Two");
    }

    @Test
    public void whenGetAllBrandsShouldReturnListOfAllDTOBrands() throws Exception {
        List<Brand> brands = Arrays.asList(brand1, brand2);
        when(brandService.findAll(any(), any())).thenReturn(brands);

        mockMvc.perform(get("/api/v1/brands"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].brandName", is(brand1.getName())));
    }

    @Test
    public void shenGetOneBrandShouldReturnBrandDTObyName() throws Exception {
        String brandName = brand1.getName();
        when(brandService.findByName(brandName)).thenReturn(Optional.ofNullable(brand1));

        mockMvc.perform(get("/api/v1/brands/{brandName}", brandName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brandName", is(brand1.getName())));
    }

    @Test
    @WithMockUser
    public void whenDeleteBrandWithValidParamShouldDeleteBrandFromDbAndReturnNoContentStatus() throws Exception {
        String brandName = brand1.getName();
        when(brandService.deleteByName(brandName)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/brands/{brandName}", brandName))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser
    public void whenDeleteBrandWithInvalidParamShouldDeleteBrandFromDbAndReturnBadRequest() throws Exception {
        String brandName = brand1.getName();
        when(brandService.deleteByName(brandName)).thenReturn(0);

        mockMvc.perform(delete("/api/v1/brands/{brandName}", brandName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(brandName + " was not deleted!"));
    }

    @Test
    @WithMockUser
    public void whenPostAddBrandShouldSaveBrandToDbAndReturnCreatedStatus() throws Exception {
        BrandDTO brandDTO = new BrandDTO("New_Brand");

        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(brandDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/brands/" + brandDTO.getBrandName()));

        verify(brandService).add(any(Brand.class));
    }

    @Test
    @WithMockUser
    public void whenPutUpdateBrandShouldUpdateBrandInDbAndReturnOk() throws Exception {
        String brandName = brand1.getName();
        BrandDTO brandDTO = new BrandDTO("New_Brand");
        when(brandService.findByName(brandName)).thenReturn(Optional.ofNullable(brand1));

        mockMvc.perform(put("/api/v1/brands/{brandName}", brandName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(brandDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(brandDTO.getBrandName())));

        verify(brandService).update(any(Brand.class), any(Brand.class));
    }
}