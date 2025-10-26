package be.kdg.sa.deliveryservice.domain.order;

import be.kdg.sa.deliveryservice.domain.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
@Slf4j
public record OrderId(UUID id) {
    public static OrderId create() {return new OrderId(UUID.randomUUID());}

    public NotFoundException notFound() {
        log.error("Order with id {} not found", id);
        return new NotFoundException("Order [" + id + "] not found");
    }
}
