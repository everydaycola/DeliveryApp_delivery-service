package be.kdg.sa.deliveryservice.api.courier.dtos;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record FullSummaryDto(
        double total,
        List<CourierSummaryDto> allCompletedDeliveries
) {
    public static FullSummaryDto from(Map<Courier, List<Delivery>> couriersWithDeliveries) {
        List<CourierSummaryDto> allCompletedDeliveries = new ArrayList<>();
        couriersWithDeliveries.forEach((courier, deliveries) -> {
            allCompletedDeliveries.add(
                    new CourierSummaryDto(
                            CourierDto.from(courier),
                            CompletedDeliveriesDto.from(deliveries)
                    )
            );
        });
        return new FullSummaryDto(
                allCompletedDeliveries.stream()
                        .map(cs -> cs.completedDeliveries.totalPayout())
                        .reduce(0.0, Double::sum),
                allCompletedDeliveries
        );
    }

    private record CourierSummaryDto(
        CourierDto courier,
        CompletedDeliveriesDto completedDeliveries
    ){}
}
