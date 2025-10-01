package be.kdg.sa.deliveryservice.application;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveries;

    public DeliveryService(@Qualifier("dbDeliveryInMemory") DeliveryRepository deliveries) {
        this.deliveries = deliveries;
    }

    public List<Delivery> findAll() {
        return deliveries.getDeliveries();
    }

    public Courier findCourierById(final CourierId courierId) {
        return deliveries.findCourierById(courierId)
                     .orElseThrow(courierId::notFound);
    }

    public Delivery confirm(DeliveryId deliveryId, CourierId courierId) {
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        final Courier courier = deliveries.findCourierById(courierId).orElseThrow(courierId::notFound);
        if (courier.HasOrder()) {
            throw new IllegalStateException("You already have an order.");
        }
        delivery.claim(courierId);
        courier.claim(deliveryId);
        deliveries.save(delivery);
        return delivery;
    }
}
