package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    public static final String KDG_EXCHANGE_NAME = "kdg_exchange";

    public static final String ORDER_ACCEPTED_QUEUE_NAME= "delivery_order_accepted_queue";

    @Bean
    TopicExchange kdgExchange() {
        return new TopicExchange(KDG_EXCHANGE_NAME);
    }

    @Bean
    Queue orderAcceptedQueue() {
        return QueueBuilder.nonDurable(ORDER_ACCEPTED_QUEUE_NAME).build();
    }

    @Bean
    Binding orderAcceptedBinding(TopicExchange kdgExchange) {
        return BindingBuilder.bind(orderAcceptedQueue()).to(kdgExchange).with("order.accepted.#");
    }

}
