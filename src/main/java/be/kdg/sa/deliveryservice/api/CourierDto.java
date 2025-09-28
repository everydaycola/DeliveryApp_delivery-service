package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.order.OrderId;

import java.util.UUID;

public record CourierDto(UUID id,
                         UUID orderId){
    public static CourierDto from(final Courier courier) {

        OrderId OrderId = courier.getOrderId();
        UUID OrderUUID;

        if  (OrderId == null) {
            OrderUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        } else {
            OrderUUID = OrderId.id();
        }

        return new CourierDto(courier.getId().id(),
                              OrderUUID);
    }
}