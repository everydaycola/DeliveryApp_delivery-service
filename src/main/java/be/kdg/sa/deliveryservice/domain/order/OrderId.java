package be.kdg.sa.deliveryservice.domain.order;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject public record OrderId(UUID id) {
    public static OrderId create() {return new OrderId(UUID.randomUUID());}

}
