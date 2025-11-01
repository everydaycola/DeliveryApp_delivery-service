package be.kdg.sa.deliveryservice.domain.courier;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CourierRepository {
    Optional<Courier> findById(final CourierId courierId);
    List<Courier> findAllByIdIn(Set<CourierId> ids);
    void save(Courier courier);
}
