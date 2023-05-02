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
import ua.com.foxminded.carrestservice.dto.CategoryDTO;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.security.SecurityConfig;
import ua.com.foxminded.carrestservice.services.CategoryService;
import ua.com.foxminded.carrestservice.utils.DTOconverters.CategoryDTOConverter;

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

@WebMvcTest(CategoryController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(SecurityConfig.class)
public class CategoryControllerTest {
    @MockBean
    private CategoryService service;

    @Autowired
    private MockMvc mockMvc;

    private Category category1;
    private Category category2;

    @BeforeAll
    public void setUp() {
        CategoryDTOConverter.setCategoryService(service);
        category1 = new Category("Category_One");
        category2 = new Category("Category_Two");
    }

    @Test
    public void getAllCategoriesShouldReturnListOfAllDTOCategories() throws Exception {
        List<Category> categories = Arrays.asList(category1, category2);
        when(service.findAll(any(), any())).thenReturn(categories);

        mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryName", is(category1.getName())));
    }

    @Test
    public void getOneCategoryShouldReturnOneCategoryDTOByName() throws Exception {
        when(service.findByName(category1.getName())).thenReturn(Optional.ofNullable(category1));

        mockMvc.perform(get("/api/v1/categories/{category}", category1.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName", is(category1.getName())));
    }

    @Test
    @WithMockUser
    public void whenDeleteCategoryWithValidParamShouldRemoveCategoryAndReturnNoContentStatus() throws Exception {
        String categoryName = category1.getName();
        when(service.delete(categoryName)).thenReturn(1);

        mockMvc.perform(delete("/api/v1/categories/{category}", categoryName))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string( ""));
    }

    @Test
    @WithMockUser
    public void whenDeleteCategoryWithInvalidParamShouldReturnBadRequest() throws Exception {
        String categoryName = "None_category";
        when(service.delete(categoryName)).thenReturn(0);

        mockMvc.perform(delete("/api/v1/categories/{category}", categoryName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string("Category " + categoryName + " was not found!"));
    }

    @Test
    @WithMockUser
    public void whenPostAddCategoryWithValidParamShouldAddCategoryToDBAndReturnCreatedStatus() throws Exception {
        String categoryName = category1.getName();
        when(service.findByName(categoryName)).thenReturn(Optional.empty());
        CategoryDTO categoryDTO = new CategoryDTO(categoryName);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/" + categoryName));

        verify(service).save(any(Category.class));
    }

    @Test
    @WithMockUser
    public void whenPostAddCategoryThatAlreadyExistsShouldReturnBadRequest() throws Exception {
        String categoryName = category1.getName();
        when(service.findByName(categoryName)).thenReturn(Optional.ofNullable(category1));
        CategoryDTO categoryDTO = new CategoryDTO(categoryName);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(categoryName + " already exists!")));
    }

    @Test
    @WithMockUser
    public void whenPutUpdateCategoryWithValidParamShouldUpdateCategoryInDBAndReturnStatusOk() throws Exception {
        String categoryName = category1.getName();
        when(service.findByName(categoryName)).thenReturn(Optional.ofNullable(category1));
        CategoryDTO categoryDTO = new CategoryDTO("New_Category");

        mockMvc.perform(put("/api/v1/categories/{categoryName}", categoryName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(categoryDTO.getCategoryName())));
    }

    @Test
    @WithMockUser
    public void whenPutUpdateCategoryWithInvalidParamShouldReturnBadRequest() throws Exception {
        String categoryName = category1.getName();
        when(service.findByName(categoryName)).thenReturn(Optional.ofNullable(category1));
        CategoryDTO categoryDTO = new CategoryDTO("X");

        mockMvc.perform(put("/api/v1/categories/{categoryName}", categoryName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",is("Category should be between 2 and 32 characters!")));
    }
}