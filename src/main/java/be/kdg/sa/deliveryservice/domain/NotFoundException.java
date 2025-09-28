package be.kdg.sa.deliveryservice.domain;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        super(message);
    }
}
