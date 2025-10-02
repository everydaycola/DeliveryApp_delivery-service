package be.kdg.sa.deliveryservice.infrastructure.courier;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.courier.CourierRepository;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierEntity;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierRepository;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DbCourierRepository implements CourierRepository {

    private final JpaCourierRepository jpaCourierRepository;
    // todo this is not correct
    private final JpaDeliveryRepository jpaDeliveryRepository;

    public DbCourierRepository(JpaCourierRepository jpaCourierRepository, JpaDeliveryRepository jpaDeliveryRepository) {
        this.jpaCourierRepository = jpaCourierRepository;
        this.jpaDeliveryRepository = jpaDeliveryRepository;
    }

    @Override
    public Optional<Courier> findById(CourierId CourierId) {
        return this.jpaCourierRepository.findById(CourierId.id())
                .map(JpaCourierEntity::toDomain);
    }

    @Override
    public void save(Courier courier) {
        JpaCourierEntity jpaCourierEntity = JpaCourierEntity.fromDomain(courier, jpaDeliveryRepository);
        this.jpaCourierRepository.save(jpaCourierEntity);
    }
}
