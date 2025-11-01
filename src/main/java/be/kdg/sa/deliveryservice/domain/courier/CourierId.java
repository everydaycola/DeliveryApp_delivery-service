package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.annotation.Identity;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
@Slf4j
public record CourierId(UUID id) {
    public static CourierId create() {return new CourierId(UUID.randomUUID());}

    public NotFoundException notFound() {
        log.error("Courier with id {} not found", id);
        return new NotFoundException("Courier [" + id + "] not found");
    }
}

