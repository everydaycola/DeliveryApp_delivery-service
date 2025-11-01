package be.kdg.sa.deliveryservice.application;

import be.kdg.sa.deliveryservice.config.DomainProperties;
import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.courier.CourierRepository;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryStatus;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveries;
    private final CourierRepository couriers;
    private final DomainProperties domainProperties;

    public DeliveryService(DeliveryRepository deliveries, CourierRepository couriers, DomainProperties domainProperties) {
        this.deliveries = deliveries;
        this.couriers = couriers;
        this.domainProperties = domainProperties;
    }

    public void createNewDelivery(OrderId orderId) {
        log.info("Creating new delivery for order {}", orderId);
        final Delivery delivery = new Delivery(DeliveryId.create(), orderId);
        deliveries.save(delivery);
    }

    public Courier findCourierById(final CourierId courierId) {
        log.info("Finding courier with id: {}", courierId);
        return couriers.findById(courierId)
                .orElseThrow(courierId::notFound);
    }

    public Delivery claim(DeliveryId deliveryId, CourierId courierId) {
        log.info("Claiming delivery with id: {}", deliveryId);
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        final Courier courier = couriers.findById(courierId).orElseThrow(courierId::notFound);
        delivery.claim(courierId);
        courier.claim(deliveryId);
        deliveries.save(delivery);
        couriers.save(courier);
        return delivery;
    }

    public List<Delivery> findCompletedDeliveries(CourierId courierId) {
        log.info("Finding completed deliveries for courier with id: {}", courierId);
        return deliveries.findCompletedDeliveriesFor(courierId);
    }

    public List<Delivery> findAllUnclaimed() {
        log.info("Finding all unclaimed deliveries");
        return deliveries.findAllByStatus(DeliveryStatus.UNCLAIMED);
    }

    public Courier unClaim(CourierId courierId) {
        log.info("Un-claiming courier with id: {}", courierId);
        final Courier courier = couriers.findById(courierId).orElseThrow(courierId::notFound);
        final DeliveryId oldDeliveryId = courier.unClaim();
        final Delivery delivery = deliveries.findById(oldDeliveryId).orElseThrow(oldDeliveryId::notFound);
        delivery.authenticate(courierId);
        delivery.unClaim();
        couriers.save(courier);
        deliveries.save(delivery);
        return courier;
    }

    public Delivery ready(DeliveryId deliveryId, CourierId courierId) {
        log.info("Setting delivery with id: {} to ready", deliveryId);
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        delivery.authenticate(courierId);
        delivery.setReadyNow();
        deliveries.save(delivery);
        return delivery;
    }

    public void ready(OrderId orderId) {
        final Delivery delivery = deliveries.findDeliveryByOrderId(orderId).orElseThrow(orderId::notFound);
        this.ready(delivery.getId(), delivery.getCourierId());
    }

    public Delivery pickup(DeliveryId deliveryId, CourierId courierId) {
        log.info("Setting delivery with id: {} to in delivery", deliveryId);
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        delivery.authenticate(courierId);
        delivery.pickUp();
        deliveries.save(delivery);
        return delivery;
    }

    public Delivery deliver(DeliveryId deliveryId, CourierId courierId) {
        log.info("Setting delivery with id: {} to delivered", deliveryId);
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        final Courier courier = couriers.findById(delivery.getCourierId()).orElseThrow(delivery.getCourierId()::notFound);
        delivery.authenticate(courierId);
        delivery.finishNow(
                domainProperties.getBasePayout(),
                domainProperties.getPerMinutePayout(),
                domainProperties.getMinimumMinutes(),
                domainProperties.getMaximumMinutes()
        );
        courier.finishDelivery();
        deliveries.save(delivery);
        couriers.save(courier);
        return delivery;
    }

    public Delivery findDelivery(DeliveryId deliveryId, CourierId courierId) {
        log.info("Finding delivery with id: {} for courier with id: {}", deliveryId, courierId);
        final Delivery delivery = deliveries.findById(deliveryId).orElseThrow(deliveryId::notFound);
        delivery.authenticate(courierId);
        return delivery;
    }
}
