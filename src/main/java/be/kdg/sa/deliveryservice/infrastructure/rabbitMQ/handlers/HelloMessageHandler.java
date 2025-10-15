package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.handlers;

import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.RabbitMQTopology;
import be.kdg.sa.deliveryservice.infrastructure.rabbitMQ.messages.HelloMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class HelloMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(HelloMessageHandler.class);

    @RabbitListener(queues = RabbitMQTopology.HELLO_QUEUE_NAME)
    void onHelloMessageReceived(HelloMessage message) {
        log.info("hello: {}", message);
    }

    @RabbitListener(queues = RabbitMQTopology.SOMETHING_QUEUE_NAME)
    void onSomethingMessageReceived(HelloMessage message) {
        log.info("something: {}", message);
    }
}
