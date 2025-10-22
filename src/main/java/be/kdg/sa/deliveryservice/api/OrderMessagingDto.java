package be.kdg.sa.deliveryservice.api;

import java.util.UUID;

public record OrderMessagingDto(UUID id, UUID restaurantId) {
}
