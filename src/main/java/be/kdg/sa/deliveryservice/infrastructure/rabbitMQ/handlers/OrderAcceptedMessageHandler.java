package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.handlers;

import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.RabbitMQTopology;
import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages.OrderAcceptedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderAcceptedMessageHandler {

    public OrderAcceptedMessageHandler() {

    }

    @RabbitListener(queues = RabbitMQTopology.RESTAURANT_ACCEPTED_QUEUE_NAME)
    void onOrderAcceptedMessageReceived(OrderAcceptedMessage message) {
        log.info("Order Accepted Message Received: Order={}", message.orderDto().id());


    }
}
