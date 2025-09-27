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

    public DeliveryId getId() {
        return id;
    }

    public OrderId getOrderId() {
        return orderId;
    }
}
