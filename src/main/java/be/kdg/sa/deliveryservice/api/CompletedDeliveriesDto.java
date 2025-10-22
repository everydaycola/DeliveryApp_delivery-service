package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public record CompletedDeliveriesDto(
        double totalPayout,
        List<CompletedDeliveryDto> completedDeliveries
) {
    public static CompletedDeliveriesDto from(final List<Delivery> deliveries) {
        log.info("Creating CompletedDeliveriesDto from deliveries");
        return new CompletedDeliveriesDto(
                deliveries.stream().map(Delivery::getPayout).reduce(0.0, Double::sum),
                deliveries.stream().map(CompletedDeliveryDto::from).toList()
        );
    }
}
