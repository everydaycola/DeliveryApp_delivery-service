package be.kdg.sa.deliveryservice.infrastructure.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    public static final String DEMO_EXCHANGE_NAME = "demo-exchange";
    public static final String HELLO_QUEUE_NAME = "hello-queue";
    public static final String SOMETHING_QUEUE_NAME = "something-queue";

    @Bean
    TopicExchange demoExchange() {
        return new TopicExchange(DEMO_EXCHANGE_NAME);
    }

    @Bean
    Queue helloQueue() {
        return QueueBuilder.nonDurable(HELLO_QUEUE_NAME).build();
    }

    @Bean
    Queue somethingQueue() {
        return QueueBuilder.nonDurable(SOMETHING_QUEUE_NAME).build();
    }

    @Bean
    Binding helloQueueToDemoExchangeBinding() {
        return BindingBuilder.bind(helloQueue()).to(demoExchange()).with("say.hello.*");
    }

    @Bean
    Binding somethingQueueToDemoExchangeBinding() {
        return BindingBuilder.bind(somethingQueue()).to(demoExchange()).with("say.something.*");
    }
}
