package be.kdg.sa.deliveryservice.infrastructure;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository public class DbDeliveryInMemory implements DeliveryRepository {

    private final List<Delivery> deliveries;
    private final List<Courier> couriers;

    public DbDeliveryInMemory() {
        this.deliveries = DataSeeder.seedDeliveries();
        this.couriers = DataSeeder.seedCouriers();
    }

    @Override public List <Delivery> getDeliveries() {
        return deliveries.stream().toList();
    }

    @Override public List <Delivery> getOpenDeliveries() {
        // todo filter
        return deliveries.stream().toList();
    }

    @Override public Optional <Courier> findCourierById(CourierId courierId) {
        return couriers.stream().filter(c -> c.getId().equals(courierId)).findFirst();
    }
}
