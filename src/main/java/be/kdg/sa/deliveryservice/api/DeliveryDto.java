package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryDto(
        UUID id,
        UUID orderId,
        UUID courierId,
        Boolean isSuccessful,
        LocalDateTime startTime,
        LocalDateTime endTime,
        double payout
) {
    public static DeliveryDto from(final Delivery delivery) {
        return new DeliveryDto(
                delivery.getId().id(),
                delivery.getOrderId().id(),
                delivery.getCourierId() == null ? null : delivery.getCourierId().id(),
                delivery.isSuccessful(),
                delivery.getStartTime(),
                delivery.getEndTime(),
                delivery.getPayout()
        );
    }
}
