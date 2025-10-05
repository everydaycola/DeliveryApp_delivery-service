package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import org.jmolecules.ddd.annotation.Entity;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Courier {
    private final CourierId id;
    private DeliveryId currentDelivery;
    private final List <DeliveryId> pastDeliveries;
    // some field to link it to the identity class

    public Courier(CourierId id) {
        this.id = id;
        this.currentDelivery = null;
        this.pastDeliveries = new ArrayList<>();

    }

    public Boolean hasOrder() {
        return currentDelivery != null;
    }

    public DeliveryId getOrderId() {
        return currentDelivery;
    }

    public CourierId getId() {
        return id;
    }

    public List<DeliveryId> getPastDeliveries() {return pastDeliveries;}

    public void setOrderId(DeliveryId deliveryId) {
        this.currentDelivery = deliveryId;
    }

    public void addPastDelivery(DeliveryId deliveryId) {
        this.pastDeliveries.add(deliveryId);
    }

    public void claim(DeliveryId deliveryId) {
        this.currentDelivery = deliveryId;
    }
}
