package be.kdg.sa.deliveryservice.api;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;

import java.util.List;

public record CompletedDeliveriesDto(
        double totalPayout,
        List<CompletedDeliveryDto> completedDeliveries
) {
    public static CompletedDeliveriesDto from(final List<Delivery> deliveries) {
        return new CompletedDeliveriesDto(
                deliveries.stream().map(Delivery::getPayout).reduce(0.0, Double::sum),
                deliveries.stream().map(CompletedDeliveryDto::from).toList()
        );
    }
}
