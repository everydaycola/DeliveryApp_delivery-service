package be.kdg.sa.deliveryservice.domain.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository {
    List <Delivery> findAll();
    List<Delivery> findAllByStatus(final DeliveryStatus status);
    Optional <Delivery> findById(final DeliveryId deliveryId);
    List<Delivery> findCompletedDeliveriesFor(CourierId courierId);
    void save(Delivery delivery);

}
