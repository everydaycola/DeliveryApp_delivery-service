package be.kdg.sa.deliveryservice.api;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record OrderMessagingDto(UUID id, UUID restaurantId) {
}
