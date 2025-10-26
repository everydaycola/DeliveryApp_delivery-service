package be.kdg.sa.deliveryservice.infrastructure.delivery.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<JpaDeliveryEntity, UUID> {

    Optional<List<JpaDeliveryEntity>> findAllByCourierIdAndStatus(UUID courierId, String status);
    Optional<List<JpaDeliveryEntity>> findAllByStatus(String status);
    Optional<JpaDeliveryEntity> findByOrderId(UUID orderId);
}
