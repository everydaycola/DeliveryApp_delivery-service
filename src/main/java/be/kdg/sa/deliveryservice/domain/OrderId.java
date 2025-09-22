package be.kdg.sa.deliveryservice.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record OrderId(UUID value) {
    public static OrderId create() {
        return new OrderId(UUID.randomUUID());
    }
}
