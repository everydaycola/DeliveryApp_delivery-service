package be.kdg.sa.deliveryservice.domain.delivery;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository {
    List <Delivery> getDeliveries();
    List<Delivery> getOpenDeliveries();
    Optional <Courier> findCourierById(final CourierId CourierId);
}
