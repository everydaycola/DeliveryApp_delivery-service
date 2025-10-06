package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.courier.Courier;

import java.util.UUID;

public record CourierDto(UUID id,
                         UUID orderId){
    public static CourierDto from(final Courier courier) {

        return new CourierDto(
                courier.getId().id(),
                courier.getCurrentDeliveryId() == null ? null : courier.getCurrentDeliveryId().id()
        );
    }
}