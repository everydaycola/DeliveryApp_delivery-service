package be.kdg.sa.deliveryservice.domain.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {
    List<Delivery> findAllByStatus(final DeliveryStatus status);
    Optional <Delivery> findById(final DeliveryId deliveryId);
    List<Delivery> findCompletedDeliveriesFor(CourierId courierId);
    List<Delivery> findAllCompletedDeliveriesBetween(LocalDateTime start, LocalDateTime end);
    void save(Delivery delivery);
    Optional<Delivery> findDeliveryByOrderId(OrderId orderId);

}
