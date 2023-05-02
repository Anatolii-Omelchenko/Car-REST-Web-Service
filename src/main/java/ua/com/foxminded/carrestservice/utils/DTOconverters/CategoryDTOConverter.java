package ua.com.foxminded.carrestservice.utils.DTOconverters;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.foxminded.carrestservice.dto.CategoryDTO;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.services.CategoryService;
import ua.com.foxminded.carrestservice.utils.exceptions.CarDataException;

import java.util.Optional;

@Service
public class CategoryDTOConverter {
    private static CategoryService categoryService;

    @Autowired
    public CategoryDTOConverter(CategoryService categoryService) {
        CategoryDTOConverter.categoryService = categoryService;
    }

    public static CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(category.getName());
    }

    public static Category convertFromDTO(CategoryDTO categoryDTO) {
        String categoryName = categoryDTO.getCategoryName();
        Optional<Category> optCategory = categoryService.findByName(categoryName);
        if (optCategory.isPresent()) {
            throw new CarDataException(categoryName + " already exists!");
        }
        return new Category(categoryName);
    }

    public static void setCategoryService(CategoryService categoryService) {
        CategoryDTOConverter.categoryService = categoryService;
    }
}