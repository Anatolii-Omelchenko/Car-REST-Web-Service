package ua.com.foxminded.carrestservice.utils.DTOconverters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.foxminded.carrestservice.dto.BrandDTO;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.services.BrandService;
import ua.com.foxminded.carrestservice.utils.exceptions.CarDataException;

import java.util.Optional;

@Service
public class BrandDTOConverter {

    private static BrandService brandService;

    @Autowired
    public BrandDTOConverter(BrandService brandService) {
        BrandDTOConverter.brandService = brandService;
    }

    public static BrandDTO convertToDTO(Brand brand) {
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setBrandName(brand.getName());
        return brandDTO;
    }

    public static Brand convertFromDTO(BrandDTO brandDTO) {
        String brandName = brandDTO.getBrandName();
        Optional<Brand> optBrand = brandService.findByName(brandName);
        if (optBrand.isPresent()) {
            throw new CarDataException(brandName + " already exists!");
        }
        return new Brand(brandName);
    }

    public static void setBrandService(BrandService brandService){
        BrandDTOConverter.brandService = brandService;
    }
}