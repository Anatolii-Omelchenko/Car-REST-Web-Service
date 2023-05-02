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
import ua.com.foxminded.carrestservice.dto.ModelDTO;
import ua.com.foxminded.carrestservice.models.Model;
import ua.com.foxminded.carrestservice.services.ModelService;
import ua.com.foxminded.carrestservice.utils.DTOconverters.ModelDTOConverter;
import ua.com.foxminded.carrestservice.utils.exceptions.ModelNotFoundException;
import ua.com.foxminded.carrestservice.utils.specifications.ModelSpecification;
import ua.com.foxminded.carrestservice.utils.specifications.SortCriteria;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ua.com.foxminded.carrestservice.utils.ControllerUtils.handleBindingErrors;
import static ua.com.foxminded.carrestservice.utils.DTOconverters.ModelDTOConverter.convertFromDTO;
import static ua.com.foxminded.carrestservice.utils.DTOconverters.ModelDTOConverter.convertToDTO;

@RestController
@RequestMapping("/api/v1/models")
public class ModelController {

    private final ModelService modelService;
    private final int PAGE_SIZE = 3;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @Operation(summary = "Get all models")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all models",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ModelDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Models not found",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping
    public List<ModelDTO> getModels(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "model") String filter,
                                    @RequestParam(defaultValue = "ASC") String sortDirection) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        SortCriteria sortCriteria = new SortCriteria(filter, sortDirection);
        ModelSpecification modelSpecification = new ModelSpecification(sortCriteria);
        List<Model> models = modelService.findAll(modelSpecification, pageable);

        if (models.isEmpty()) {
            throw new ModelNotFoundException("Models was not found!");
        }

        return models.stream().map(ModelDTOConverter::convertToDTO).collect(Collectors.toList());
    }

    @Operation(summary = "Get one model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the model",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ModelDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Models not found",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping("/{brand}/{model}/{year}")
    public ModelDTO getOneModel(@PathVariable("brand") String brand,
                                @PathVariable("model") String model,
                                @PathVariable("year") int year) {
        return convertToDTO(modelService.findByNameAndProductionYearAndBrandName(model, year, brand)
                .orElseThrow(() -> new ModelNotFoundException("Model was not found.")));
    }

    @Operation(summary = "Delete model", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the model",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Model was not deleted",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Model not found",
                    content = @Content(mediaType = "application/json"))})
    @DeleteMapping("/{brand}/{model}/{year}")
    public ResponseEntity<?> deleteModel(@PathVariable("brand") String brand,
                                         @PathVariable("model") String model,
                                         @PathVariable("year") int year) {
        if (modelService.delete(brand, year, model) == 1) {
            return ResponseEntity.noContent().build();
        }
        String message = "Model was not found.";
        return ResponseEntity.badRequest().body(message);
    }

    @Operation(summary = "Add new model", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Add the model",
                    headers = @Header(name = "Location", description = "URI of the created model"),
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json"))})
    @PostMapping
    public ResponseEntity<?> addModel(@RequestBody @Valid ModelDTO modelDTO, BindingResult bindingResult) {
        handleBindingErrors(bindingResult);

        Model model = convertFromDTO(modelDTO);
        modelService.save(model);
        return ResponseEntity
                .created(URI.create(String.format("/models/%s/%s/%d", modelDTO.getBrandName(), modelDTO.getModelName(), modelDTO.getProductionYear())))
                .build();
    }

    @Operation(summary = "Update model", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the model",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ModelDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Models not found",
                    content = @Content(mediaType = "application/json"))})
    @PutMapping("/{brand}/{modelName}/{year}")
    public ResponseEntity<?> updateModel(@RequestBody @Valid ModelDTO modelDTO,
                                         BindingResult bindingResult,
                                         @PathVariable("brand") String brand,
                                         @PathVariable("modelName") String modelName,
                                         @PathVariable("year") int year) {
        handleBindingErrors(bindingResult);

        Model updatedModel = convertFromDTO(modelDTO);
        Model oldModel = modelService.findByNameAndProductionYearAndBrandName(modelName, year, brand)
                .orElseThrow(() -> new ModelNotFoundException("Model was not found."));

        modelService.update(oldModel, updatedModel);
        return ResponseEntity.ok(updatedModel);
    }
}