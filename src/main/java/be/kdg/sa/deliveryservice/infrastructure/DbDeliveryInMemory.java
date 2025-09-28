package be.kdg.sa.deliveryservice.infrastructure;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository public class DbDeliveryInMemory implements DeliveryRepository {

    private List<Delivery> deliveries;

    public DbDeliveryInMemory() {
        this.deliveries = DataSeeder.seedDeliveries();
    }

    @Override public List <Delivery> getDeliveries() {
        return deliveries.stream().toList();
    }

    @Override public List <Delivery> getOpenDeliveries() {
        // todo filter
        return deliveries.stream().toList();
    }
}
