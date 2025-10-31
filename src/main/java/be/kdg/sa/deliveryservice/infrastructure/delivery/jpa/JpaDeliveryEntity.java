package be.kdg.sa.deliveryservice.infrastructure.delivery.jpa;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryStatus;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "Deliveries")
@AllArgsConstructor
public class JpaDeliveryEntity {
    @Getter
    @Id
    @Column
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private JpaCourierEntity courier;

    @Column(nullable = false)
    private String status;

    @Column()
    private LocalDateTime startTime;

    @Column()
    private LocalDateTime endTime;

    @Column(nullable = false)
    private double payout;

    protected JpaDeliveryEntity() {} // for JPA

    public static JpaDeliveryEntity fromDomain(Delivery delivery,  JpaCourierEntity jpaCourierEntity) {
        return new JpaDeliveryEntity(
                delivery.getId().id(),
                delivery.getOrderId().id(),
                jpaCourierEntity,
                delivery.getStatus().toString(),
                delivery.getStartTime() == null ? null : delivery.getStartTime(),
                delivery.getEndTime() == null ? null : delivery.getEndTime(),
                delivery.getPayout()
        );
    }

    public Delivery toDomain() {
        return new Delivery(
                new DeliveryId(this.id),
                new OrderId(this.orderId),
                Optional.ofNullable(this.courier)
                        .map(JpaCourierEntity::getId)
                        .map(CourierId::new)
                        .orElse(null),
                DeliveryStatus.valueOf(this.status),
                this.startTime,
                this.endTime,
                this.payout);
    }

}
