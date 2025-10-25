package be.kdg.sa.deliveryservice.api.delivery.dtos;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
public record DeliveryDto(
        UUID id,
        UUID orderId,
        UUID courierId,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        double payout
) {
    public static DeliveryDto from(final Delivery delivery) {
        log.info("Creating DeliveryDto from delivery {}", delivery.getId());
        return new DeliveryDto(
                delivery.getId().id(),
                delivery.getOrderId().id(),
                delivery.getCourierId() == null ? null : delivery.getCourierId().id(),
                delivery.getStatus().toString(),
                delivery.getStartTime(),
                delivery.getEndTime(),
                delivery.getPayout()
        );
    }
}
