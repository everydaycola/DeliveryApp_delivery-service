package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.order.OrderId;

import java.util.UUID;

public record CourierDto(UUID id,
                         UUID orderId){
    public static CourierDto from(final Courier courier) {

        return new CourierDto(
                courier.getId().id(),
                courier.getOrderId() == null ? null : courier.getOrderId().id()
        );
    }
}