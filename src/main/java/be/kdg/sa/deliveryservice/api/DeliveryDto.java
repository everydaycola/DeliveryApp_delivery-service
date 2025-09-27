package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;

import java.util.UUID;

public record DeliveryDto(UUID id,
                          UUID orderId,
                          UUID courierId){
    public static DeliveryDto from(final Delivery delivery) {
        return new DeliveryDto(delivery.getId().id(),
                               delivery.getOrderId().id(),
                               delivery.getCourierId().id());
    }
}
