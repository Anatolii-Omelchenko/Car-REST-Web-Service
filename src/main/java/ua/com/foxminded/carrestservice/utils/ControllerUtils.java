package ua.com.foxminded.carrestservice.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ua.com.foxminded.carrestservice.utils.exceptions.CarDataException;

import java.util.List;

public class ControllerUtils {
    public static void handleBindingErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            errors.forEach(err -> errorMessage.append(err.getField())
                    .append(" - ").append(err.getDefaultMessage())
                    .append(";"));

            throw new CarDataException(errorMessage.toString());
        }
    }
}