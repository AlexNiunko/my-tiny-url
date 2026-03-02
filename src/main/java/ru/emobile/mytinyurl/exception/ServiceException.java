package ru.emobile.mytinyurl.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {

    private final HttpStatus httpStatus;

    public ServiceException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
