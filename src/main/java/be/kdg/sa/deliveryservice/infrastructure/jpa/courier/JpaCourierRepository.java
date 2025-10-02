package be.kdg.sa.deliveryservice.infrastructure.jpa.courier;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCourierRepository  extends JpaRepository <JpaCourierEntity, UUID> {

}
