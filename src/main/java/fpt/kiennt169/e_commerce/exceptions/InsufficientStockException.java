package fpt.kiennt169.e_commerce.exceptions;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BaseException {

    private static final String ERROR_CODE = "INSUFFICIENT_STOCK";

    public InsufficientStockException(String sku, int requested, int available) {
        super(
            String.format("Insufficient stock for SKU '%s'. Requested: %d, Available: %d", sku, requested, available),
            HttpStatus.BAD_REQUEST,
            ERROR_CODE
        );
    }

    public InsufficientStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ERROR_CODE);
    }
}
