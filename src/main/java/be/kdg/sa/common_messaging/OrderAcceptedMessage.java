package be.kdg.sa.common_messaging;

import be.kdg.sa.deliveryservice.api.OrderAcceptedDto;

public record OrderAcceptedMessage(OrderAcceptedDto orderDto) {
}
