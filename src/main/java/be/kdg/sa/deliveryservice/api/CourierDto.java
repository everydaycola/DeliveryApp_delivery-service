package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public record CourierDto(UUID id,
                         String name,
                         UUID deliveryId){
    public static CourierDto from(final Courier courier) {
        log.info("Creating CourierDto from courier {}", courier.getId());
        return new CourierDto(
                courier.getId().id(),
                courier.getName(),
                courier.getCurrentDeliveryId() == null ? null : courier.getCurrentDeliveryId().id()
        );
    }
}