package be.kdg.sa.deliveryservice.domain.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.annotation.Entity;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Entity
@Slf4j
@AllArgsConstructor
public class Delivery {

    private final DeliveryId id;
    private final OrderId orderId;
    private CourierId courierId;
    private DeliveryStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double payout;

    public Delivery(DeliveryId id, OrderId orderId) {
        log.info("Creating delivery {}", id);
        this.id = id;
        this.orderId = orderId;
        this.status = DeliveryStatus.UNCLAIMED;
    }

    public void claim(CourierId courierId) {
        log.info("Claiming delivery {} for courier {}", this.id, courierId);
        this.status.shouldBe(DeliveryStatus.UNCLAIMED);
        this.courierId = courierId;
        this.status = DeliveryStatus.CLAIMED;
    }

    public void unClaim() {
        log.info("Unclaiming delivery {}", this.id);
        this.status.shouldBe(DeliveryStatus.CLAIMED);
        this.courierId = null;
        this.status = DeliveryStatus.UNCLAIMED;
    }

    public void setReadyNow() {
        log.info("Setting ready for pickup for delivery {}", this.id);
        this.status.shouldBe(DeliveryStatus.CLAIMED);
        this.startTime = LocalDateTime.now();
        this.status = DeliveryStatus.READY_FOR_PICKUP;
    }

    public void pickUp() {
        log.info("Picking up delivery {}", this.id);
        this.status.shouldBe(DeliveryStatus.READY_FOR_PICKUP);
        this.status = DeliveryStatus.IN_DELIVERY;
    }

    public void finishNow(double basePayout, double perMinutePayout, double minimumMinutes, double maximumMinutes) {
        log.info("Finishing delivery {}", this.id);
        this.status.shouldBe(DeliveryStatus.IN_DELIVERY);
        this.endTime = LocalDateTime.now();
        double minutes = Math.ceil(Duration.between(this.startTime, this.endTime).getSeconds() / 60.0);
        this.payout = basePayout + (perMinutePayout * Math.clamp(minutes, minimumMinutes, maximumMinutes));
        this.status = DeliveryStatus.DELIVERED;
    }

    public void authenticate(CourierId courierId) {
        log.info("Authenticating delivery {} for courier {}", this.id, courierId);
        if (this.courierId != null && !this.courierId.equals(courierId)) {
            throw new IllegalStateException("Courier " + courierId.toString() + " does not own delivery " + this.id.toString());
        }
    }
}
