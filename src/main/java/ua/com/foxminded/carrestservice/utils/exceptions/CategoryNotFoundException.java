package ua.com.foxminded.carrestservice.utils.exceptions;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends CarServiceException {
    public CategoryNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}