package be.kdg.sa.deliveryservice.domain.delivery;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository {
    List <Delivery> getDeliveries();
    List<Delivery> getOpenDeliveries();
}
