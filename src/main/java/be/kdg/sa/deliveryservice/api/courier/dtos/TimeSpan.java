package be.kdg.sa.deliveryservice.api.courier.dtos;

import java.time.LocalDateTime;

public record TimeSpan(
        LocalDateTime start,
        LocalDateTime end
) {
}
