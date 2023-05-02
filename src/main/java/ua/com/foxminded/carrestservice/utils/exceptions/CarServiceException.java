package ua.com.foxminded.carrestservice.utils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CarServiceException extends RuntimeException{
    private final HttpStatus status;

    public CarServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}