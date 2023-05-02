package ua.com.foxminded.carrestservice.utils.exceptions;

import org.springframework.http.HttpStatus;

public class BrandNotFoundException extends CarServiceException{
    public BrandNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}