package be.kdg.sa.deliveryservice.api.courier.dtos;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@ValueObject
public record CompletedDeliveriesDto(
        double totalPayout,
        List<CompletedDeliveryDto> completedDeliveries
) {
    public static CompletedDeliveriesDto from(final List<Delivery> deliveries) {
        log.info("Creating CompletedDeliveriesDto from deliveries");
        return new CompletedDeliveriesDto(
                deliveries.stream().map(Delivery::getPayout).reduce(0.0, Double::sum),
                deliveries.stream().map(delivery -> new CompletedDeliveryDto(
                        delivery.getStartTime(),
                        delivery.getEndTime(),
                        delivery.getPayout()
                )).toList()
        );
    }

    private record CompletedDeliveryDto(
            LocalDateTime startTime,
            LocalDateTime endTime,
            double payout
    ) {
    }
}
