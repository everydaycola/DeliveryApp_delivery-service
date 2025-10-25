package be.kdg.sa.deliveryservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @AllArgsConstructor
@ConfigurationProperties(prefix = "spring.rabbitmq.kdg")
public class RabbitMQProperties {
    private final String exchangeName;
    private final String orderAcceptedQueue;
    private final String orderAcceptedBinding;
    private final String orderReadyQueue;
    private final String orderReadyBinding;
    private final String orderPickedUpBinding;
    private final String orderDeliveredBinding;
}
