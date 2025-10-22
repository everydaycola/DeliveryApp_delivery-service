package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    public static final String KDG_EXCHANGE_NAME = "kdg_exchange";
    public static final String DELIVERY_QUEUE_NAME = "delivery_queue";

    @Bean
    TopicExchange kdgExchange() {
        return new TopicExchange(KDG_EXCHANGE_NAME);
    }

    @Bean
    Queue deliveryQueue() {
        return QueueBuilder.nonDurable(DELIVERY_QUEUE_NAME).build();
    }

    @Bean
    Binding deliveryQueueBindingRestaurantEvents(TopicExchange kdgExchange) {
        return BindingBuilder.bind(deliveryQueue()).to(kdgExchange).with("restaurant.*");
    }

    @Bean
    Binding deliveryQueueBindingOrderEvents(TopicExchange kdgExchange) {
        return BindingBuilder.bind(deliveryQueue()).to(kdgExchange).with("order.*");
    }
}
