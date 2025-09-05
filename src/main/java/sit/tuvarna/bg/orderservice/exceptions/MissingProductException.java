package sit.tuvarna.bg.orderservice.exceptions;

public class MissingProductException extends RuntimeException {
    public MissingProductException(String message) {
        super(message);
    }
}
