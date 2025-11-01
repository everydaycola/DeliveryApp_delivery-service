package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record OrderPickedUpAndDeliveredDto(UUID id) {
    public static OrderPickedUpAndDeliveredDto from(Delivery delivery){
        return new OrderPickedUpAndDeliveredDto(delivery.getOrderId().id());
    }
}
