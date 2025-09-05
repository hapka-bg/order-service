package sit.tuvarna.bg.orderservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sit.tuvarna.bg.orderservice.exceptions.*;
import sit.tuvarna.bg.orderservice.web.dto.error.ApiError;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(AuthFeignServiceNotWorkingException.class)
    public ResponseEntity<ApiError> handleAuthServiceNotWorkingException(AuthFeignServiceNotWorkingException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }
    @ExceptionHandler(CloudServiceException.class)
    public ResponseEntity<ApiError> handleCloudNotWorkingException(CloudServiceException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }
    @ExceptionHandler(IngredientNotFoundException.class)
    public ResponseEntity<ApiError> handleIngredientNotFoundException(IngredientNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }
    @ExceptionHandler(InvalidPromoCodeException.class)
    public ResponseEntity<ApiError> handleInvalidPromoCodeException(InvalidPromoCodeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }
    @ExceptionHandler({MissingOrderException.class,MissingProductException.class, MissingPromoCodeException.class})
    public ResponseEntity<ApiError> handleInvalidPromoCodeException(MissingOrderException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(OwnerOrderException.class)
    public ResponseEntity<ApiError> handleOwnerOrderException(OwnerOrderException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String message) {
        ApiError error = ApiError.builder()
                .code(status.toString())
                .message(message)
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
