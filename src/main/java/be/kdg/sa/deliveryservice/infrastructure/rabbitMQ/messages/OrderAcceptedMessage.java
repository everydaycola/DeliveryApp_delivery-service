package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages;

import be.kdg.sa.deliveryservice.api.OrderMessagingDto;

public record OrderAcceptedMessage(OrderMessagingDto orderDto) {
}
