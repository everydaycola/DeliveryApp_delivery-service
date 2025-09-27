package be.kdg.sa.deliveryservice.infrastructure.jpa;

import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Deliveries")
public class JpaDeliveryEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column()
    private UUID deliveryId;

    protected JpaDeliveryEntity() {} // for JPA

    public JpaDeliveryEntity(UUID id, UUID orderId, UUID deliveryId) {
        this.id = id;
        this.orderId = orderId;
        this.deliveryId = deliveryId;
    }

    public static JpaDeliveryEntity fromDomain(Delivery delivery) {
        JpaDeliveryEntity jpaDeliveryEntity = new JpaDeliveryEntity(delivery.getId().id(),
                                                         delivery.getOrderId().id(),
                                                         delivery.getCourierId().id());
        return jpaDeliveryEntity;
    }
}
