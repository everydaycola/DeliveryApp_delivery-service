package be.kdg.sa.deliveryservice.infrastructure.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryStatus;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierEntity;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierRepository;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class DbDeliveryRepository implements DeliveryRepository {

    private final JpaDeliveryRepository jpaDeliveryRepository;
    private final JpaCourierRepository jpaCourierRepository;

    public DbDeliveryRepository(JpaDeliveryRepository jpaCourierRepository, JpaCourierRepository jpaCourierRepository1) {
        this.jpaDeliveryRepository = jpaCourierRepository;
        this.jpaCourierRepository = jpaCourierRepository1;
    }

    @Override
    public Optional <Delivery> findDeliveryByOrderId(OrderId orderId) {
        log.info("Finding delivery for order with id {}", orderId.id());
        return this.jpaDeliveryRepository.findByOrderId(orderId.id())
                .map(JpaDeliveryEntity::toDomain);
    }

    @Override public List <Delivery> findAllByStatus(DeliveryStatus status) {
        log.info("Finding all deliveries with status {}", status);
        return jpaDeliveryRepository.findAllByStatus(status.toString())
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .map(JpaDeliveryEntity::toDomain)
                                    .toList();
    }

    @Override
    public Optional<Delivery> findById(DeliveryId deliveryId) {
        log.info("Finding delivery with id {}", deliveryId.id());
        return this.jpaDeliveryRepository.findById(deliveryId.id())
                .map(JpaDeliveryEntity::toDomain);
    }

    @Override public List<Delivery> findCompletedDeliveriesFor(CourierId courierId) {
        log.info("Finding completed deliveries for courier with id {}", courierId.id());
        return this.jpaDeliveryRepository.findAllByCourierIdAndStatus(courierId.id(), "DELIVERED")
                                         .orElse(Collections.emptyList())
                                         .stream()
                                         .map(JpaDeliveryEntity::toDomain)
                                         .toList();
    }

    @Override
    public List<Delivery> findAllCompletedDeliveriesBetween(LocalDateTime start, LocalDateTime end) {
        return this.jpaDeliveryRepository.findAllByStatusAndStartTimeIsAfterAndEndTimeIsBefore(
                DeliveryStatus.DELIVERED.toString(),
                start, end)
                .orElse(Collections.emptyList())
                .stream()
                .map(JpaDeliveryEntity::toDomain)
                .toList();
    }

    @Override
    public void save(Delivery delivery) {
        log.info("Saving delivery {}", delivery.getId());
        JpaCourierEntity courier = Optional.ofNullable(delivery.getCourierId())
                .flatMap(courierId -> jpaCourierRepository.findById(courierId.id()))
                .orElse(null);

        JpaDeliveryEntity jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, courier);
        this.jpaDeliveryRepository.save(jpaDeliveryEntity);

    }
}
