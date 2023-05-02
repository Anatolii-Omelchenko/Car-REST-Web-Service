package ua.com.foxminded.carrestservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category should not be empty!")
    @Size(min = 2, max = 32, message = "Category should be between 2 and 32 characters!")
    private String categoryName;
}