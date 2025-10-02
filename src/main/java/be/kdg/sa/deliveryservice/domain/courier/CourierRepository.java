package be.kdg.sa.deliveryservice.domain.courier;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepository {
    Optional<Courier> findById(final CourierId CourierId);
    void save(Courier courier);
}
