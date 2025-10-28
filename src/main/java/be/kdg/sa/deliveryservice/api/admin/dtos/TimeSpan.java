package be.kdg.sa.deliveryservice.api.admin.dtos;

import java.time.LocalDateTime;

public record TimeSpan(
        LocalDateTime start,
        LocalDateTime end
) {
}
