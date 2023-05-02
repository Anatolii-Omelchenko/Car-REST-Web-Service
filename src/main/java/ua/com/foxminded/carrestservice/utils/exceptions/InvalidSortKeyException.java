package ua.com.foxminded.carrestservice.utils.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidSortKeyException extends CarServiceException {
    public InvalidSortKeyException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}