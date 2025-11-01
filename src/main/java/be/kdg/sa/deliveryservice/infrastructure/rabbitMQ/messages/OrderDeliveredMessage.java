package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages;

import be.kdg.sa.deliveryservice.api.OrderPickedUpAndDeliveredDto;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record OrderDeliveredMessage(OrderPickedUpAndDeliveredDto orderDto) {
}
