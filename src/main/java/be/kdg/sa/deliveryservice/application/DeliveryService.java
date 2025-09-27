package be.kdg.sa.deliveryservice.application;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveries;

    public DeliveryService(DeliveryRepository deliveries) {
        this.deliveries = deliveries;
    }

    public List<Delivery> findAll() {
        return deliveries.getDeliveries();
    }

    public List<Delivery> findAllOpen() {
        return deliveries.getOpenDeliveries();
    }

}
