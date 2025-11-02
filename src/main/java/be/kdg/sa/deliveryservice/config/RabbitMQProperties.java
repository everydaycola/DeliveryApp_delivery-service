package be.kdg.sa.deliveryservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @AllArgsConstructor
@ConfigurationProperties(prefix = "spring.rabbitmq.kdg")
public class RabbitMQProperties {
    private final String exchangeName;
    private final String orderAcceptedDeliveryQueue;
    private final String orderAcceptedDeliveryBinding;
    private final String orderReadyDeliveryQueue;
    private final String orderReadyDeliveryBinding;
    private final String orderPickedUpBinding;
    private final String orderDeliveredBinding;
}
