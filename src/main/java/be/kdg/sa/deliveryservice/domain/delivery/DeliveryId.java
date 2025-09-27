package be.kdg.sa.deliveryservice.domain.delivery;

import java.util.UUID;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject public record DeliveryId(UUID id) {
    public static DeliveryId create() {
        return new DeliveryId(UUID.randomUUID());
    }
}
