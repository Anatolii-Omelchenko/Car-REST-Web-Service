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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.carrestservice.dto.CarDTO;
import ua.com.foxminded.carrestservice.dto.CategoryDTO;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.services.CategoryService;
import ua.com.foxminded.carrestservice.utils.DTOconverters.CategoryDTOConverter;
import ua.com.foxminded.carrestservice.utils.exceptions.CarDataException;
import ua.com.foxminded.carrestservice.utils.exceptions.CategoryNotFoundException;
import ua.com.foxminded.carrestservice.utils.specifications.CategorySpecification;
import ua.com.foxminded.carrestservice.utils.specifications.SortCriteria;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ua.com.foxminded.carrestservice.utils.DTOconverters.CategoryDTOConverter.convertFromDTO;
import static ua.com.foxminded.carrestservice.utils.DTOconverters.CategoryDTOConverter.convertToDTO;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final int PAGE_SIZE = 3;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all categories",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Categories not found",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping
    public List<CategoryDTO> getAllCategories(@RequestParam(defaultValue = "ASC") String sortDirection,
                                              @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        SortCriteria sortCriteria = new SortCriteria("none", sortDirection);
        CategorySpecification spec = new CategorySpecification(sortCriteria);

        List<Category> categories = categoryService.findAll(spec, pageable);
        if (categories.isEmpty()) {
            throw new CategoryNotFoundException("Categories was not found!");
        }

        return categories.stream().map(CategoryDTOConverter::convertToDTO).collect(Collectors.toList());
    }

    @Operation(summary = "Get category by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Categories not found",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping("/{category}")
    public CategoryDTO getOneCategory(@PathVariable("category") String category) {
        return convertToDTO(categoryService.findByName(category)
                .orElseThrow(() -> new CategoryNotFoundException("Category " + category + " was not found!")));
    }

    @Operation(summary = "Delete category by name", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the category",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Category was not deleted",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json"))})
    @DeleteMapping("/{category}")
    public ResponseEntity<?> deleteCategory(@PathVariable("category") String category) {
        if (categoryService.delete(category) == 1) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body("Category " + category + " was not found!");
    }

    @Operation(summary = "Add new category", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Add the category",
                    headers = @Header(name = "Location", description = "URI of the created category"),
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json"))})
    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody @Valid CategoryDTO categoryDTO, BindingResult bindingResult) {
        Category category = convertFromDTO(categoryDTO);
        categoryService.save(category);
        return ResponseEntity.created(URI.create("/categories/" + category.getName())).build();
    }

    @Operation(summary = "Update category by name", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the category",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Categories not found",
                    content = @Content(mediaType = "application/json"))})
    @PutMapping("/{category}")
    public ResponseEntity<?> updateCategory(@RequestBody @Valid CategoryDTO categoryDTO,
                                            BindingResult bindingResult,
                                            @PathVariable("category") String categoryName) {
        if (bindingResult.hasFieldErrors("categoryName")) {
            throw new CarDataException(bindingResult.getFieldError("categoryName").getDefaultMessage());
        }

        Category oldCategory = categoryService.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category " + categoryName + " was not found!"));
        Category newCategory = convertFromDTO(categoryDTO);

        categoryService.update(oldCategory, newCategory);

        return ResponseEntity.ok(newCategory);
    }
}