package be.kdg.sa.deliveryservice;

import be.kdg.sa.deliveryservice.config.DomainProperties;
import be.kdg.sa.deliveryservice.config.RabbitMQProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({DomainProperties.class, RabbitMQProperties.class})
public class DeliveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }
}
