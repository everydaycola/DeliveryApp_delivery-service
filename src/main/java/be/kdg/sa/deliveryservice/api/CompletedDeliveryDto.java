package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompletedDeliveryDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        double payout
) {
    public static CompletedDeliveryDto from(final Delivery delivery) {
        return new CompletedDeliveryDto(
                delivery.getStartTime(),
                delivery.getEndTime(),
                delivery.getPayout()
        );
    }
}
