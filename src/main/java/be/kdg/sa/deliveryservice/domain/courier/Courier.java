package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.order.OrderId;
import org.jmolecules.ddd.annotation.Entity;

@Entity
public class Courier {
    private final CourierId id;
    private OrderId currentOrderId;
    // some field to link it to the identity class

    public Courier(CourierId id) {
        this.id = id;
    }

    public OrderId getOrderId() {
        return currentOrderId;
    }

    public void setOrderId(OrderId orderId) {
        this.currentOrderId = orderId;
    }
}
