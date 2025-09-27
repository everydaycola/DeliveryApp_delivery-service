package be.kdg.sa.deliveryservice.domain.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import org.jmolecules.ddd.annotation.Entity;

@Entity
public class Delivery {
    private final DeliveryId id;
    private final OrderId orderId;
    private CourierId courierId;
    private boolean isSuccessful;
    private long deliveryTime;
    private double payout;

    public Delivery(DeliveryId id, OrderId orderId) {
        this.id = id;
        this.orderId = orderId;
        this.isSuccessful = false;
    }

    public CourierId getCourierId() {
        return courierId;
    }

    public void setCourierId(CourierId courierId) {
        this.courierId = courierId;
    }
}
