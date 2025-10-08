package be.kdg.sa.deliveryservice.infrastructure.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryStatus;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierRepository;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbDeliveryRepository implements DeliveryRepository {

    private final JpaDeliveryRepository jpaDeliveryRepository;
    // todo this is not correct
    private final JpaCourierRepository jpaCourierRepository;

    public DbDeliveryRepository(JpaDeliveryRepository jpaCourierRepository, JpaCourierRepository jpaCourierRepository1) {
        this.jpaDeliveryRepository = jpaCourierRepository;
        this.jpaCourierRepository = jpaCourierRepository1;
    }

    @Override
    public List<Delivery> findAll() {
        return jpaDeliveryRepository.findAll().stream()
                .map(JpaDeliveryEntity::toDomain)
                .toList();
    }

    @Override public List <Delivery> findallByStatus(DeliveryStatus status) {
        return jpaDeliveryRepository.findAllByStatus(status.toString())
                                    .orElse(List.of())
                                    .stream()
                                    .map(JpaDeliveryEntity::toDomain)
                                    .toList();
    }

    @Override
    public Optional<Delivery> findById(DeliveryId deliveryId) {
        return this.jpaDeliveryRepository.findById(deliveryId.id())
                .map(JpaDeliveryEntity::toDomain);
    }

    @Override public List<Delivery> findDeliveriesFor(CourierId courierId) {
        return this.jpaDeliveryRepository.findAllByCourierIdAndStatus(courierId.id(), "DELIVERED")
                                         .orElse(List.of())
                                         .stream()
                                         .map(JpaDeliveryEntity::toDomain)
                                         .toList();
    }

    @Override
    public void save(Delivery delivery) {
        JpaDeliveryEntity jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, jpaCourierRepository);
        this.jpaDeliveryRepository.save(jpaDeliveryEntity);
    }
}
