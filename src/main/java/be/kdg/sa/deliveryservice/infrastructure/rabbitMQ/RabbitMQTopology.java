package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ;

import be.kdg.sa.deliveryservice.config.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    private final RabbitMQProperties properties;

    public RabbitMQTopology(RabbitMQProperties properties) {this.properties = properties;}

    @Bean
    TopicExchange kdgExchange() {
        return new TopicExchange(properties.getExchangeName());
    }

    @Bean
    Queue orderAcceptedQueue() {
        return QueueBuilder.nonDurable(properties.getOrderAcceptedDeliveryQueue()).build();
    }

    @Bean
    Queue orderReadyQueue() {
        return QueueBuilder.nonDurable(properties.getOrderReadyDeliveryQueue()).build();
    }

    @Bean
    Binding orderAcceptedDeliveryBinding() {
        return BindingBuilder.bind(orderAcceptedQueue()).to(kdgExchange()).with(properties.getOrderAcceptedDeliveryBinding());
    }

    @Bean
    Binding orderReadyBinding(){
        return BindingBuilder.bind(orderReadyQueue()).to(kdgExchange()).with(properties.getOrderReadyDeliveryBinding());
    }
}
