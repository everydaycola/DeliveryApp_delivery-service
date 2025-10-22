package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.handlers;

import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.RabbitMQTopology;
import be.kdg.sa.common_messaging.OrderAcceptedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitMQTopology.DELIVERY_QUEUE_NAME)
@Slf4j
public class OrderAcceptedMessageHandler {

    public OrderAcceptedMessageHandler() {

    }

    @RabbitHandler
    void onOrderAcceptedMessageReceived(OrderAcceptedMessage message) {
        log.info("Order Accepted Message Received: Order={}", message.orderDto().id());


    }
}
