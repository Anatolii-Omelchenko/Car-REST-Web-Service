package ua.com.foxminded.carrestservice.utils.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.com.foxminded.carrestservice.utils.errors.ErrorResponse;

@RestControllerAdvice
public class CarServiceExceptionHandler {
    @ExceptionHandler(CarServiceException.class)
    private ResponseEntity<ErrorResponse> handleException(CarServiceException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, ex.getStatus());
    }
}