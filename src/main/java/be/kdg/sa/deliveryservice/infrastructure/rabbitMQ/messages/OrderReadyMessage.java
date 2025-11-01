package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages;

import be.kdg.sa.deliveryservice.api.OrderMessagingDto;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record OrderReadyMessage(OrderMessagingDto orderDto) {
}
