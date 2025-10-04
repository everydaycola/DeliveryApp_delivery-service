package be.kdg.sa.deliveryservice.infrastructure.delivery.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<JpaDeliveryEntity, UUID> {

    Optional<List<JpaDeliveryEntity>> findAllByCourierIdAndIsSuccessfulTrue(UUID courierId);
}
