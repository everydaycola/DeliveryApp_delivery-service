package be.kdg.sa.deliveryservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "domain")
public class DomainProperties {
    private double basePayout;
    private double perMinutePayout;
    private double minimumMinutes;
    private double maximumMinutes;
}
