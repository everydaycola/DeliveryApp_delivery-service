package be.kdg.sa.deliveryservice.domain.delivery;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.annotation.ValueObject;

@Getter
@Slf4j
@ValueObject
public enum DeliveryStatus {
    UNCLAIMED(0),
    CLAIMED(1),
    READY_FOR_PICKUP(2),
    IN_DELIVERY(3),
    DELIVERED(4);

    private final int phase;

    DeliveryStatus(int phase) {
        this.phase = phase;
    }

    private String getName() {
        return this.name().replace("_", " ").toLowerCase();
    }

    public void shouldBe(DeliveryStatus this, DeliveryStatus that ) {
        log.info("Checking if status {} is {}", this.getName(), that.getName());
        if (!this.equals(that)) {
            log.error("Delivery status should be {} but is {}", that.getName(), this.getName());
            throw new IllegalStateException(
                    "Delivery status should be " + that.getName() + " but is " + this.getName()
            );
        }
    }

}
