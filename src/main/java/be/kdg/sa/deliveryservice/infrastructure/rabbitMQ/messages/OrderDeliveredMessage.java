package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages;

import be.kdg.sa.deliveryservice.api.OrderPickedUpAndDeliveredDto;

public record OrderDeliveredMessage(OrderPickedUpAndDeliveredDto orderDto) {
}
