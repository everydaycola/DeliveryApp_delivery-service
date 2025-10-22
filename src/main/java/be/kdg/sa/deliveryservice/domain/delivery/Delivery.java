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

    private static final double BASE_PAYOUT = 3;
    private static final double PER_MINUTE_PAYOUT = 0.3;
    private static final double MINIMUM_MINUTES = 5;
    private static final double MAXIMUM_MINUTES = 30;

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
        setReadyAt(LocalDateTime.now());
    }

    public void setReadyAt(LocalDateTime startTime) {
        log.info("Setting ready for pickup for delivery {}", this.id);
        this.status.shouldBe(DeliveryStatus.CLAIMED);
        this.startTime = startTime;
        this.status = DeliveryStatus.READY_FOR_PICKUP;
    }

    public void pickUp() {
        log.info("Picking up delivery {}", this.id);
        this.status.shouldBe(DeliveryStatus.READY_FOR_PICKUP);
        this.status = DeliveryStatus.IN_DELIVERY;
    }

    public void finishNow() {
        finishAt(LocalDateTime.now());
    }

    public void finishAt(LocalDateTime endTime) {
        log.info("Finishing delivery {}", this.id);
        this.status.shouldBe(DeliveryStatus.IN_DELIVERY);
        this.endTime = endTime;
        this.payout = calculatePayout(this.startTime, endTime);
        this.status = DeliveryStatus.DELIVERED;
    }

    private double calculatePayout(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Calculating payout for delivery {}", this.id);
        double minutes = Math.ceil(Duration.between(startTime, endTime).getSeconds() / 60.0);
        return BASE_PAYOUT + (PER_MINUTE_PAYOUT * Math.clamp(minutes, MINIMUM_MINUTES, MAXIMUM_MINUTES));
    }

    public void authenticate(CourierId courierId) {
        log.info("Authenticating delivery {} for courier {}", this.id, courierId);
        if (this.courierId != null && !this.courierId.equals(courierId)) {
            throw new IllegalStateException("Courier " + courierId.toString() + " does not own delivery " + this.id.toString());
        }
    }

    public void overRidePayment(double payout) {
        log.info("Overriding payout for delivery {}", this.id);
        this.payout = payout;
    }
}
