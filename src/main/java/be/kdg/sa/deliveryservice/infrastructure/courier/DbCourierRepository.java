package be.kdg.sa.deliveryservice.infrastructure.courier;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.courier.CourierRepository;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierEntity;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierRepository;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class DbCourierRepository implements CourierRepository {

    private final JpaCourierRepository jpaCourierRepository;
    private final JpaDeliveryRepository jpaDeliveryRepository;

    public DbCourierRepository(JpaCourierRepository jpaCourierRepository, JpaDeliveryRepository jpaDeliveryRepository) {
        this.jpaCourierRepository = jpaCourierRepository;
        this.jpaDeliveryRepository = jpaDeliveryRepository;
    }

    @Override
    public Optional<Courier> findById(CourierId courierId) {
        log.info("Finding courier with id {}", courierId.id());
        return this.jpaCourierRepository.findById(courierId.id())
                .map(JpaCourierEntity::toDomain);
    }

    @Override
    public List<Courier> findAllByIdIn(Set<CourierId> ids) {
        return this.jpaCourierRepository.findAllByIdIn(
                    ids.stream().map(CourierId::id).toList()
                ).stream()
                .map(JpaCourierEntity::toDomain)
                .toList();
    }

    @Override
    public void save(Courier courier) {
        log.info("Saving courier {}", courier.getId());
        JpaDeliveryEntity currentDelivery = Optional.ofNullable(courier.getCurrentDeliveryId())
                .flatMap(deliveryId -> jpaDeliveryRepository.findById(deliveryId.id()))
                .orElse(null);

        List<JpaDeliveryEntity> pastDeliveries = Optional.ofNullable(courier.getPastDeliveries())
                .orElse(Collections.emptyList())
                .stream()
                .map(deliveryId -> jpaDeliveryRepository.findById(deliveryId.id()))
                .flatMap(Optional::stream)
                .toList();

        JpaCourierEntity jpaCourierEntity = JpaCourierEntity
                .fromDomain(courier, currentDelivery, pastDeliveries);
        this.jpaCourierRepository.save(jpaCourierEntity);
    }
}
