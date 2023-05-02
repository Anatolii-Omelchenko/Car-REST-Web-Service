package ua.com.foxminded.carrestservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {

    @NotEmpty(message = "Number should not be empty!")
    @Size(min = 4, max = 12, message = "Number should be between 4 and 12 characters!")
    private String number;

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