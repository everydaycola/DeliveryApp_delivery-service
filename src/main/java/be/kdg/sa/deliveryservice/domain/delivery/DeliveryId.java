package be.kdg.sa.deliveryservice.domain.delivery;

import java.util.UUID;

import be.kdg.sa.deliveryservice.domain.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
@Slf4j
public record DeliveryId(UUID id) {
    public static DeliveryId create() {
        return new DeliveryId(UUID.randomUUID());
    }

    public NotFoundException notFound() {
        log.error("Delivery with id {} not found", id);
        return new NotFoundException("Delivery [" + id + "] not found");
    }
}
