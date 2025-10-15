package be.kdg.sa.deliveryservice.domain.delivery;

import lombok.Getter;

@Getter
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
        if (!this.equals(that)) {
            throw new IllegalStateException(
                    "Delivery status should be " + that.getName() + " but is " + this.getName()
            );
        }
    }

}
