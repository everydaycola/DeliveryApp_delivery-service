package be.kdg.sa.deliveryservice.api.admin.dtos;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;

@ValueObject
public record TimeSpan(
        LocalDateTime start,
        LocalDateTime end
) {
}
