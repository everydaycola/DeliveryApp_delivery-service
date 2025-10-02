package be.kdg.sa.deliveryservice.domain.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import org.jmolecules.ddd.annotation.Entity;

import java.time.LocalDateTime;

@Entity public class Delivery {
    private final DeliveryId id;
    private final OrderId orderId;
    private CourierId courierId;
    private boolean isSuccessful;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double payout;

    public Delivery(DeliveryId id, OrderId orderId) {
        this.id = id;
        this.orderId = orderId;
        this.isSuccessful = false;
    }

    public CourierId getCourierId() {
        return courierId;
    }

    public void setCourierId(CourierId courierId) {
        this.courierId = courierId;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void claimNow(CourierId courierId) {
        claimAt(courierId, LocalDateTime.now());
    }

    public void claimAt(CourierId courierId, LocalDateTime startTime) {
        if (this.courierId != null) {
            throw new IllegalStateException("Delivery is already claimed");
        }
        this.courierId = courierId;
        this.startTime = startTime;
    }

    public void finishNow() {
        finishAt(LocalDateTime.now());
    }

    public void finishAt(LocalDateTime endTime) {
        if (this.courierId == null) {
            throw new IllegalStateException("Delivery is not claimed");
        }
        if (this.startTime == null) {
            throw new IllegalStateException("Delivery has not started yet");
        }
        if (this.endTime != null || this.isSuccessful) {
            throw new IllegalStateException("Delivery has not started yet");
        }
        this.endTime = endTime;
        this.isSuccessful = true;
    }

    public DeliveryId getId() {
        return id;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public boolean isSuccessful() {
        return isSuccessful;
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
