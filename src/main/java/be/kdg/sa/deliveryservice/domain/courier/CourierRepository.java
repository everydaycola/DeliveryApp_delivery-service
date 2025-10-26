package be.kdg.sa.deliveryservice.domain.courier;

import java.util.Optional;

public interface CourierRepository {
    Optional<Courier> findById(final CourierId courierId);
    void save(Courier courier);
}
