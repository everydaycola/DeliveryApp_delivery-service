package be.kdg.sa.deliveryservice.domain.courier;

import java.util.*;

public interface CourierRepository {
    Optional<Courier> findById(final CourierId courierId);
    List<Courier> findAllByIdIn(Set<CourierId> ids);
    void save(Courier courier);
}
