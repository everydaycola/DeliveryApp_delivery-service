package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import org.jmolecules.ddd.annotation.Entity;

@Entity
public class Courier {
    private final CourierId id;
    private DeliveryId currentDeliveryId;
    // some field to link it to the identity class

    public Courier(CourierId id) {
        this.id = id;
    }

    public Boolean HasOrder() {
        return currentDeliveryId != null;
    }

    public DeliveryId getOrderId() {
        return currentDeliveryId;
    }

    public CourierId getId() {
        return id;
    }

    public void setOrderId(DeliveryId deliveryId) {
        this.currentDeliveryId = deliveryId;
    }

    public void claim(DeliveryId deliveryId) {
        this.currentDeliveryId = deliveryId;
    }
}
