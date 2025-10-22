package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
public record CompletedDeliveryDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        double payout
) {
    public static CompletedDeliveryDto from(final Delivery delivery) {
        log.info("Creating CompletedDeliveryDto from delivery {}", delivery.getId());
        return new CompletedDeliveryDto(
                delivery.getStartTime(),
                delivery.getEndTime(),
                delivery.getPayout()
        );
    }
}
