package ua.com.foxminded.carrestservice.utils.exceptions;

import org.springframework.http.HttpStatus;

public class ModelNotFoundException extends CarServiceException {
    public ModelNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}