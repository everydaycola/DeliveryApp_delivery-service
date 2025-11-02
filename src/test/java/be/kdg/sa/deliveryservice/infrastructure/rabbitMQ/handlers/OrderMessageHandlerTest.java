package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.handlers;

import be.kdg.sa.deliveryservice.api.OrderMessagingDto;
import be.kdg.sa.deliveryservice.application.DeliveryService;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages.OrderAcceptedMessage;
import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages.OrderReadyMessage;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderMessageHandlerTest {

    @Mock
    private DeliveryService deliveryService;

    @InjectMocks
    private OrderMessageHandler sut;

    @Test
    void onOrderAcceptedMessageReceivedShouldSucceedAndCallDeliveryService() {
        // arrange
        final var orderUUID = UUID.randomUUID();
        final var orderId = new OrderId(orderUUID);
        final var restaurantUUID = UUID.randomUUID();
        final var orderMessage = new OrderAcceptedMessage(new OrderMessagingDto(orderUUID, restaurantUUID));
        // act
        sut.onOrderAcceptedMessageReceived(orderMessage);
        // assert
        verify(deliveryService).createNewDelivery(orderId);
    }

    @Test
    void onOrderReadyMessageReceivedShouldSucceedAndCallDeliveryService() {
        // arrange
        final var orderUUID = UUID.randomUUID();
        final var orderId = new OrderId(orderUUID);
        final var restaurantUUID = UUID.randomUUID();
        final var orderMessage = new OrderReadyMessage(new OrderMessagingDto(orderUUID, restaurantUUID));
        // act
        sut.onOrderReadyMessageReceived(orderMessage);
        // assert
        verify(deliveryService).ready(orderId);
    }
}