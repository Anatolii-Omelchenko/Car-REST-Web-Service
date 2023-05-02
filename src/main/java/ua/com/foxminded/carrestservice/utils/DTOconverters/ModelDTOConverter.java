package ua.com.foxminded.carrestservice.utils.DTOconverters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.foxminded.carrestservice.dto.ModelDTO;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.models.Category;
import ua.com.foxminded.carrestservice.models.Model;
import ua.com.foxminded.carrestservice.services.BrandService;
import ua.com.foxminded.carrestservice.services.CategoryService;
import ua.com.foxminded.carrestservice.services.ModelService;
import ua.com.foxminded.carrestservice.utils.exceptions.CarDataException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ModelDTOConverter {

    private static ModelService modelService;
    private static BrandService brandService;
    private static CategoryService categoryService;

    @Autowired
    public ModelDTOConverter(ModelService modelService, BrandService brandService, CategoryService categoryService) {
        ModelDTOConverter.brandService = brandService;
        ModelDTOConverter.modelService = modelService;
        ModelDTOConverter.categoryService = categoryService;
    }

    public static ModelDTO convertToDTO(Model model) {
        ModelDTO modelDTO = new ModelDTO();
        modelDTO.setBrandName(model.getBrand().getName());
        modelDTO.setModelName(model.getName());
        modelDTO.setProductionYear(model.getProductionYear());

        Set<String> categories = model.getCategories().stream().map(Category::getName).collect(Collectors.toSet());
        modelDTO.setCategories(categories);

        return modelDTO;
    }

    public static Model convertFromDTO(ModelDTO modelDTO) {
        Optional<Model> modelOpt = modelService
                .findByNameAndProductionYearAndBrandName(modelDTO.getModelName(), modelDTO.getProductionYear(), modelDTO.getBrandName());
        Set<Category> categories = getSetOfCategories(modelDTO.getCategories());

        if (modelOpt.isPresent() && modelOpt.get().getCategories().equals(categories)) {
            throw new CarDataException("This model is already exists!");
        }

        Brand brand = brandService.findByName(modelDTO.getBrandName()).orElseThrow(() -> new CarDataException("Brand was not found!"));

        Model model = new Model();
        model.setName(modelDTO.getModelName());
        model.setBrand(brand);
        model.setProductionYear(modelDTO.getProductionYear());

        model.setCategories(categories);

        return model;
    }

    public static void setServices(ModelService modelService, BrandService brandService, CategoryService categoryService) {
        ModelDTOConverter.brandService = brandService;
        ModelDTOConverter.modelService = modelService;
        ModelDTOConverter.categoryService = categoryService;
    }

    private static Set<Category> getSetOfCategories(Set<String> strCategories) {
        return strCategories
                .stream()
                .map(name -> categoryService.findByName(name).orElseThrow())
                .collect(Collectors.toSet());
    }
}