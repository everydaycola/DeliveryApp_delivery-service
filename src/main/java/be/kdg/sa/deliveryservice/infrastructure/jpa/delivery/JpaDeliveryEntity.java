package be.kdg.sa.deliveryservice.infrastructure.jpa.delivery;

import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import be.kdg.sa.deliveryservice.infrastructure.jpa.courier.JpaCourierEntity;
import be.kdg.sa.deliveryservice.infrastructure.jpa.courier.JpaCourierRepository;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Deliveries")
public class JpaDeliveryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private JpaCourierEntity courier;

    @Column(nullable = false)
    private boolean isSuccessful;

    @Column()
    private LocalDateTime startTime;

    @Column()
    private LocalDateTime endTime;

    @Column()
    private double payout;

    protected JpaDeliveryEntity() {} // for JPA

    public JpaDeliveryEntity(UUID id, UUID orderId) {
        this.id = id;
        this.orderId = orderId;
    }

    public static JpaDeliveryEntity fromDomain(Delivery delivery, JpaCourierRepository courierRepository) {
        JpaDeliveryEntity jpaDeliveryEntity = new JpaDeliveryEntity(delivery.getId().id(),
                                                                    delivery.getOrderId().id());

        // Set courier if delivery has one assigned
        if (delivery.getCourierId() != null) {
            // or throw an exception if courier must exist
            jpaDeliveryEntity.courier = courierRepository.findById(delivery.getCourierId().id())
                                                         .orElse(null);
        }

        // Set other properties
        jpaDeliveryEntity.isSuccessful = delivery.isSuccessful();
        jpaDeliveryEntity.startTime = delivery.getStartTime() == null ? null : delivery.getStartTime();
        jpaDeliveryEntity.endTime = delivery.getEndTime() == null ? null : delivery.getEndTime();
        jpaDeliveryEntity.payout = delivery.getPayout();

        return jpaDeliveryEntity;
    }

    public static Delivery toDomain(JpaDeliveryEntity jpaDeliveryEntity) {
        Delivery delivery = new Delivery(
                new DeliveryId(jpaDeliveryEntity.id),
                new OrderId(jpaDeliveryEntity.orderId)
        );
        if (jpaDeliveryEntity.courier == null) {
            return delivery;
        }
        delivery.claimAt(new CourierId(jpaDeliveryEntity.courier.getId()), jpaDeliveryEntity.startTime);
        if (!jpaDeliveryEntity.isSuccessful) {
            return delivery;
        }
        delivery.finishAt(jpaDeliveryEntity.endTime);
        return delivery;
    }

    // Add getter and setter for courier
    public JpaCourierEntity getCourier() {
        return courier;
    }

    public void setCourier(JpaCourierEntity courier) {
        this.courier = courier;
    }
}
