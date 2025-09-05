package sit.tuvarna.bg.orderservice.exceptions;

public class CloudServiceException extends RuntimeException {
    public CloudServiceException(String message) {
        super(message);
    }
}
