package sit.tuvarna.bg.orderservice.exceptions;

public class MissingPromoCodeException extends RuntimeException {
    public MissingPromoCodeException(String message) {
        super(message);
    }
}
