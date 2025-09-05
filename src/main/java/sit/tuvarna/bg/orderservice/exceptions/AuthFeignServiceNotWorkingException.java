package sit.tuvarna.bg.orderservice.exceptions;

public class AuthFeignServiceNotWorkingException extends RuntimeException {
    public AuthFeignServiceNotWorkingException(String message) {
        super(message);
    }
}
