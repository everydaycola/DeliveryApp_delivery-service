package be.kdg.sa.deliveryservice.domain.courier;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject public record CourierId(UUID id) {
    public static CourierId create() {return new CourierId(UUID.randomUUID());}
}

