package ua.com.foxminded.carrestservice.utils.exceptions;

import org.springframework.http.HttpStatus;

public class CarDataException extends CarServiceException{
    public CarDataException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}