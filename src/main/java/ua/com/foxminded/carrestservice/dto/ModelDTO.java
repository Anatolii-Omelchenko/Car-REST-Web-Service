package ua.com.foxminded.carrestservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModelDTO {
    @NotEmpty(message = "Brand should not be empty!")
    @Size(min = 2, max = 32, message = "Brand should be between 2 and 32 characters!")
    private String brandName;

    @NotEmpty(message = "Model should not be empty!")
    @Size(min = 2, max = 32, message = "Model should be between 2 and 32 characters!")
    private String modelName;

    @NotNull(message = "Year of production should not be empty!")
    @Min(1886)
    private Integer productionYear;

    @NotNull
    private Set<String> categories;
}