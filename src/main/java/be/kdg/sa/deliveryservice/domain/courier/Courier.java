package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Courier {
    @Getter
    private final CourierId id;
    private DeliveryId currentDelivery;
    @Getter
    private final List <DeliveryId> pastDeliveries;
    @Getter
    private final String name;
    // some field to link it to the identity class

    public Courier(CourierId id, String name) {
        this.id = id;
        this.name = name;
        this.currentDelivery = null;
        this.pastDeliveries = new ArrayList<>();

    }

    public DeliveryId getCurrentDeliveryId() {
        return currentDelivery;
    }

    public void addPastDelivery(DeliveryId deliveryId) {
        this.pastDeliveries.add(deliveryId);
    }

    public void claim(DeliveryId deliveryId) {
        if (this.currentDelivery != null) {
            throw new IllegalStateException("Courier already has an order");
        }
        this.currentDelivery = deliveryId;
    }

    public DeliveryId unClaim() {
        if (this.currentDelivery == null) {
            throw new IllegalStateException("Courier doesn't have an order");
        }
        DeliveryId old_delivery = this.currentDelivery;
        this.currentDelivery = null;
        return old_delivery;
    }

    public void finishDelivery() {
        if (this.currentDelivery == null) {
            throw new IllegalStateException("Courier doesn't have an order");
        }
        this.pastDeliveries.add(this.currentDelivery);
        this.currentDelivery = null;
    }

}
