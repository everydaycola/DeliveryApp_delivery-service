package be.kdg.sa.deliveryservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "domain")
public class DomainProperties {
    private final double basePayout;
    private final double perMinutePayout;
    private final double minimumMinutes;
    private final double maximumMinutes;
}
