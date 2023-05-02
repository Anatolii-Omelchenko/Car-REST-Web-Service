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
import ua.com.foxminded.carrestservice.dto.BrandDTO;
import ua.com.foxminded.carrestservice.models.Brand;
import ua.com.foxminded.carrestservice.services.BrandService;
import ua.com.foxminded.carrestservice.utils.DTOconverters.BrandDTOConverter;
import ua.com.foxminded.carrestservice.utils.exceptions.BrandNotFoundException;
import ua.com.foxminded.carrestservice.utils.exceptions.CarDataException;
import ua.com.foxminded.carrestservice.utils.specifications.BrandSpecification;
import ua.com.foxminded.carrestservice.utils.specifications.SortCriteria;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ua.com.foxminded.carrestservice.utils.DTOconverters.BrandDTOConverter.convertFromDTO;
import static ua.com.foxminded.carrestservice.utils.DTOconverters.BrandDTOConverter.convertToDTO;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final BrandService brandService;
    private final int PAGE_SIZE = 3;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @Operation(summary = "Get all brands")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all brands",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BrandDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Brands not found",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping
    public List<BrandDTO> getAllBrands(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "ASC") String sortDirection) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        SortCriteria sortCriteria = new SortCriteria("none", sortDirection);
        BrandSpecification brandSpec = new BrandSpecification(sortCriteria);
        List<Brand> brands = brandService.findAll(brandSpec, pageable);

        if (brands.isEmpty()) {
            throw new BrandNotFoundException("Brands was not found!");
        }

        return brands.stream().map(BrandDTOConverter::convertToDTO).collect(Collectors.toList());
    }

    @Operation(summary = "Get brand by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the brand",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BrandDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Brands not found",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping("/{brandName}")
    public BrandDTO getOneBrand(@PathVariable("brandName") String brandName) {
        return convertToDTO(brandService.findByName(brandName)
                .orElseThrow(() -> new BrandNotFoundException("Brand was not found!")));
    }

    @Operation(summary = "Delete brand by name", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the brand",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Brand was not deleted",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Brands not found",
                    content = @Content(mediaType = "application/json"))})
    @DeleteMapping("/{brandName}")
    public ResponseEntity<?> deleteBrand(@PathVariable("brandName") String brandName) {
        if (brandService.deleteByName(brandName) == 1) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body(brandName + " was not deleted!");
    }

    @Operation(summary = "Add new brand", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Add the brand",
                    headers = @Header(name = "Location", description = "URI of the created brand"),
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json"))})
    @PostMapping
    public ResponseEntity<?> addBrand(@RequestBody @Valid BrandDTO brandDTO, BindingResult bindingResult) {
        if (bindingResult.getFieldError("name") != null) {
            String errorMsg = bindingResult.getFieldError("name").getDefaultMessage();
            throw new CarDataException(errorMsg);
        }

        brandService.add(convertFromDTO(brandDTO));
        return ResponseEntity.created(URI.create("/brands/" + brandDTO.getBrandName())).build();
    }

    @Operation(summary = "Update brand", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the brand",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BrandDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Brands not found",
                    content = @Content(mediaType = "application/json"))})
    @PutMapping("/{brandName}")
    public ResponseEntity<?> updateBrand(@PathVariable("brandName") String brandName,
                                         @RequestBody @Valid BrandDTO brandDTO, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors("brandName")) {
            String errorMsg = bindingResult.getFieldError("brandName").getDefaultMessage();
            throw new CarDataException(errorMsg);
        }

        Brand oldBrand = brandService.findByName(brandName)
                .orElseThrow(() -> new BrandNotFoundException(brandName + " was not found!"));
        Brand newBrand = convertFromDTO(brandDTO);

        brandService.update(oldBrand, newBrand);
        return ResponseEntity.ok(newBrand);
    }
}