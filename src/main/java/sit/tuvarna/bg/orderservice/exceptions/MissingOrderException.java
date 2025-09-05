package sit.tuvarna.bg.orderservice.exceptions;

public class MissingOrderException extends RuntimeException {
    public MissingOrderException(String message) {
        super(message);
    }
}
