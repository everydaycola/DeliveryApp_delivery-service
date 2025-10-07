package be.kdg.sa.deliveryservice.domain.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import org.jmolecules.ddd.annotation.Entity;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity public class Delivery {

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
        this.id = id;
        this.orderId = orderId;
        this.status = DeliveryStatus.UNCLAIMED;
    }

    public void claim(CourierId courierId) {
        this.status.shouldBe(DeliveryStatus.UNCLAIMED);
        this.courierId = courierId;
        this.status = DeliveryStatus.CLAIMED;
    }

    public void unclaim() {
        this.status.shouldBe(DeliveryStatus.CLAIMED);
        this.courierId = null;
        this.status = DeliveryStatus.UNCLAIMED;
    }

    public void setReadyNow() {
        setReadyAt(LocalDateTime.now());
    }

    public void setReadyAt(LocalDateTime startTime) {
        // check if is claimed
        this.status.shouldBe(DeliveryStatus.CLAIMED);
        this.startTime = startTime;
        this.status = DeliveryStatus.READY_FOR_PICKUP;
    }

    public void pickUp() {
        // check if is ready for pickup
        this.status.shouldBe(DeliveryStatus.READY_FOR_PICKUP);
        this.status = DeliveryStatus.IN_DELIVERY;
    }

    public void finishNow() {
        finishAt(LocalDateTime.now());
    }

    public void finishAt(LocalDateTime endTime) {
        // check if is in delivery
        this.status.shouldBe(DeliveryStatus.IN_DELIVERY);
        this.endTime = endTime;
        this.payout = calculatePayout(this.startTime, endTime);
        this.status = DeliveryStatus.DELIVERED;
    }

    private double calculatePayout(LocalDateTime startTime, LocalDateTime endTime) {
        double minutes = Math.ceil(Duration.between(startTime, endTime).getSeconds() / 60.0);
        return BASE_PAYOUT + (PER_MINUTE_PAYOUT * Math.clamp(minutes, MINIMUM_MINUTES, MAXIMUM_MINUTES));
    }

    public void setPayout(double payout) {
        this.payout = payout;
    }

    public DeliveryId getId() {
        return id;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public CourierId getCourierId() {
        return courierId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public double getPayout() {
        return payout;
    }
}
