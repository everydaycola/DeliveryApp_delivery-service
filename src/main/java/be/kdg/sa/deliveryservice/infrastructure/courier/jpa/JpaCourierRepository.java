package be.kdg.sa.deliveryservice.infrastructure.courier.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCourierRepository  extends JpaRepository <JpaCourierEntity, UUID> {
    List<JpaCourierEntity> findAllByIdIn(List<UUID> ids);
}
