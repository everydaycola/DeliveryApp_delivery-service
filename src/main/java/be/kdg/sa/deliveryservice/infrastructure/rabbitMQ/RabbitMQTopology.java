package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    public static final String KDG_EXCHANGE_NAME = "kdg_exchange";

    public static final String RESTAURANT_QUEUE_NAME = "restaurant_queue";
    public static final String RESTAURANT_ACCEPTED_QUEUE_NAME = "restaurant_accepted_delivery_queue";
    public static final String ORDER_QUEUE_NAME = "order_queue";


    @Bean
    TopicExchange kdgExchange() {
        return new TopicExchange(KDG_EXCHANGE_NAME);
    }

    @Bean
    Queue restaurantQueue() {
        return QueueBuilder.nonDurable(RESTAURANT_QUEUE_NAME).build();
    }

    @Bean
    Queue restaurantAcceptedQueue(){
        return QueueBuilder.nonDurable(RESTAURANT_ACCEPTED_QUEUE_NAME).build();
    }

    @Bean
    Queue orderQueue() {
        return QueueBuilder.nonDurable(ORDER_QUEUE_NAME).build();
    }

    @Bean
    Binding restaurantQueueToKdgExchangeBinding() {
        return BindingBuilder.bind(restaurantQueue()).to(kdgExchange()).with("restaurant.*");
    }

    @Bean
    Binding orderQueueToKdgExchangeBinding() {
        return BindingBuilder.bind(orderQueue()).to(kdgExchange()).with("order.*");
    }

    @Bean
    Binding restaurantAcceptedQueueToKdgExchangeBinding() {
        return BindingBuilder.bind(restaurantAcceptedQueue()).to(kdgExchange()).with("restaurant.accepted.#");
    }
}
