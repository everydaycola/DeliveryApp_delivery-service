package be.kdg.sa.deliveryservice.infrastructure.delivery.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<JpaDeliveryEntity, UUID> {

}
