package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.handlers;

import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages.OrderAcceptedMessage;
import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages.OrderReadyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderMessageHandler {
    //TODO: insert logic

    public OrderMessageHandler() {

    }

    @RabbitListener(queues = "${spring.rabbitmq.kdg.order-accepted-queue}")
    void onOrderAcceptedMessageReceived(OrderAcceptedMessage message) {
        log.info("Order Accepted Message Received: Order={}", message.orderDto().id());
    }

    @RabbitListener(queues = "${spring.rabbitmq.kdg.order-ready-queue}")
    void onOrderReadyMessageReceived(OrderReadyMessage message) {
        log.info("Order Ready Message Received: Order={}", message.orderDto().id());
    }
}
