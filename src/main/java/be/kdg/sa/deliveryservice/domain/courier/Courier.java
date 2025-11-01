package be.kdg.sa.deliveryservice.domain.courier;

import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.annotation.Entity;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@Slf4j
public class Courier {
    @Getter
    private final CourierId id; // same id as in keycloak
    private DeliveryId currentDelivery;
    @Getter
    private final List <DeliveryId> pastDeliveries;
    @Getter
    private final String name;

    public Courier(CourierId id, String name) {
        log.info("Creating courier {}", id);
        this.id = id;
        this.name = name;
        this.currentDelivery = null;
        this.pastDeliveries = new ArrayList<>();

    }

    public DeliveryId getCurrentDeliveryId() {
        return currentDelivery;
    }

    public void addPastDelivery(DeliveryId deliveryId) {
        log.info("Adding past delivery {} to courier {}", deliveryId, this.id);
        this.pastDeliveries.add(deliveryId);
    }

    public void claim(DeliveryId deliveryId) {
        log.info("Courier {} claimed order {}", this.id, deliveryId);
        if (this.currentDelivery != null) {
            log.warn("Courier {} claimed order {} but it was already claimed", this.id, this.currentDelivery);
            throw new IllegalStateException("Courier already has an order");
        }
        this.currentDelivery = deliveryId;
    }

    public DeliveryId unClaim() {
        if (this.currentDelivery == null) {
            log.warn("Courier {} unclaimed order but doesn't have one", this.id);
            throw new IllegalStateException("Courier doesn't have an order");
        }
        log.info("Courier {} unclaimed order {}", this.id, this.currentDelivery);
        final var oldDelivery = this.currentDelivery;
        this.currentDelivery = null;
        return oldDelivery;
    }

    public void finishDelivery() {
        if (this.currentDelivery == null) {
            log.warn("Courier {} tried finishing order but doesn't have one assigned", this.id);
            throw new IllegalStateException("Courier doesn't have an order");
        }
        log.info("Courier {} finished order {}", this.id, this.currentDelivery);
        this.pastDeliveries.add(this.currentDelivery);
        this.currentDelivery = null;
    }

}
