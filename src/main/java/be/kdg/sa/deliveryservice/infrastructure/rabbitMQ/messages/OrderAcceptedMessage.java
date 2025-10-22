package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages;

import be.kdg.sa.deliveryservice.api.OrderAcceptedDto;

public record OrderAcceptedMessage(OrderAcceptedDto orderDto) {
}
