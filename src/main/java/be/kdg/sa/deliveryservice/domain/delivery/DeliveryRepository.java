package be.kdg.sa.deliveryservice.domain.delivery;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository {
    List <Delivery> findALl();
    Optional <Delivery> findById(final DeliveryId deliveryId);
    void save(Delivery delivery);
}
