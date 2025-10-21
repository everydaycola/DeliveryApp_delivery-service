package be.kdg.sa.deliveryservice.api;

import java.util.UUID;

public record OrderAcceptedDto(UUID id, UUID restaurantId) {
}
