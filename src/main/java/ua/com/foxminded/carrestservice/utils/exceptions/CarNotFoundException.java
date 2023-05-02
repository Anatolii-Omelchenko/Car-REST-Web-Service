package ua.com.foxminded.carrestservice.utils.exceptions;

import org.springframework.http.HttpStatus;

public class CarNotFoundException extends CarServiceException{
    public CarNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}