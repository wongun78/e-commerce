package fpt.kiennt169.e_commerce.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {

    private static final String ERROR_CODE = "BAD_REQUEST";

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ERROR_CODE);
    }

    public BadRequestException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
