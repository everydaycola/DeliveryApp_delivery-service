package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject public record CourierId(UUID id) {
    public static CourierId create() {return new CourierId(UUID.randomUUID());}

    public NotFoundException notFound() {
        return new NotFoundException("Courier [" + id + "] not found");
    }
}

