package be.kdg.sa.deliveryservice.application;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.courier.CourierRepository;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveries;
    private final CourierRepository couriers;

    public DeliveryService(DeliveryRepository deliveries, CourierRepository couriers) {
        this.deliveries = deliveries;
        this.couriers = couriers;
    }

    public List<Delivery> findAll() {
        return deliveries.findAll();
    }

    public Courier findCourierById(final CourierId courierId) {
        return couriers.findById(courierId)
                .orElseThrow(courierId::notFound);
    }

    public Delivery claim(DeliveryId deliveryId, CourierId courierId) {
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        final Courier courier = couriers.findById(courierId).orElseThrow(courierId::notFound);
        delivery.claim(courierId);
        courier.claim(deliveryId);
        deliveries.save(delivery);
        couriers.save(courier);
        return delivery;
    }

    public List<Delivery> findCompletedDeliveries(CourierId courierId) {
        return deliveries.findDeliveriesFor(courierId);
    }

    public List<Delivery> findAllUnclaimed() {
        return deliveries.findAllByStatus(DeliveryStatus.UNCLAIMED);
    }

    public Courier unClaim(CourierId courierId) {
        final Courier courier = couriers.findById(courierId).orElseThrow(courierId::notFound);
        final DeliveryId oldDeliveryId = courier.unClaim();
        final Delivery delivery = deliveries.findById(oldDeliveryId)
                .orElseThrow(() -> new IllegalStateException("Previously claimed delivery not found"));
        delivery.unClaim();
        couriers.save(courier);
        deliveries.save(delivery);
        return courier;
    }

    public Delivery ready(DeliveryId deliveryId) {
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        delivery.setReadyNow();
        deliveries.save(delivery);
        return delivery;
    }

    public Delivery pickup(DeliveryId deliveryId) {
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        delivery.pickUp();
        deliveries.save(delivery);
        return delivery;
    }

    public Delivery deliver(DeliveryId deliveryId) {
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        delivery.finishNow();
        deliveries.save(delivery);
        return delivery;
    }

    public Delivery findDelivery(DeliveryId deliveryId) {
        return deliveries.findById(deliveryId)
                .orElseThrow(deliveryId::notFound);
    }
}
