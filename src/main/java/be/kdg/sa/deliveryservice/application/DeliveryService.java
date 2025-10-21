package be.kdg.sa.deliveryservice.application;

import be.kdg.sa.deliveryservice.domain.NotFoundException;
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
        return deliveries.findCompletedDeliveriesFor(courierId);
    }

    public List<Delivery> findAllUnclaimed() {
        return deliveries.findAllByStatus(DeliveryStatus.UNCLAIMED);
    }

    public Courier unClaim(CourierId courierId) {
        final Courier courier = couriers.findById(courierId).orElseThrow(courierId::notFound);
        final DeliveryId oldDeliveryId = courier.unClaim();
        final Delivery delivery = deliveries.findById(oldDeliveryId)
                .orElseThrow(() -> new NotFoundException("Previously claimed delivery not found"));
        delivery.authenticate(courierId);
        delivery.unClaim();
        couriers.save(courier);
        deliveries.save(delivery);
        return courier;
    }

    public Delivery ready(DeliveryId deliveryId, CourierId courierId) {
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        delivery.authenticate(courierId);
        delivery.setReadyNow();
        deliveries.save(delivery);
        return delivery;
    }

    public Delivery pickup(DeliveryId deliveryId, CourierId courierId) {
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        delivery.authenticate(courierId);
        delivery.pickUp();
        deliveries.save(delivery);
        return delivery;
    }

    public Delivery deliver(DeliveryId deliveryId, CourierId courierId) {
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        final Courier courier = couriers.findById(delivery.getCourierId()).orElseThrow(delivery.getCourierId()::notFound);
        delivery.authenticate(courierId);
        delivery.finishNow();
        courier.finishDelivery();
        deliveries.save(delivery);
        couriers.save(courier);
        return delivery;
    }

    public Delivery findDelivery(DeliveryId deliveryId, CourierId courierId) {
        Delivery delivery = deliveries.findById(deliveryId)
                .orElseThrow(deliveryId::notFound);
        delivery.authenticate(courierId);
        return delivery;
    }
}
