package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import org.jmolecules.ddd.annotation.Entity;

import java.util.List;

@Entity
public class Courier {
    private final CourierId id;
    private DeliveryId currentDelivery;
    private List <DeliveryId> pastDeliveries;
    // some field to link it to the identity class

    public Courier(CourierId id) {
        this.id = id;
    }

    public Boolean HasOrder() {
        return currentDelivery != null;
    }

    public DeliveryId getOrderId() {
        return currentDelivery;
    }

    public CourierId getId() {
        return id;
    }

    public void setOrderId(DeliveryId deliveryId) {
        this.currentDelivery = deliveryId;
    }

    public void claim(DeliveryId deliveryId) {
        this.currentDelivery = deliveryId;
    }
}
