package be.kdg.sa.deliveryservice.infrastructure.courier.jpa;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Couriers")
@AllArgsConstructor
public class JpaCourierEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_delivery_id")
    private JpaDeliveryEntity currentdelivery;

    @OneToMany(mappedBy = "courier", fetch = FetchType.LAZY)
    private List<JpaDeliveryEntity> pastDeliveries;

    @Column
    private String name;

    protected JpaCourierEntity() {
    }

    public static JpaCourierEntity fromDomain(Courier courier, JpaDeliveryEntity currentDelivery, List<JpaDeliveryEntity> pastDeliveries) {
        return new JpaCourierEntity(
                courier.getId().id(),
                currentDelivery,
                pastDeliveries,
                courier.getName()
        );
    }

    public Courier toDomain() {
        return new Courier(
                new CourierId(this.id),
                this.currentdelivery == null ? null : new DeliveryId(this.currentdelivery.getId()),
                this.pastDeliveries.stream().map(delivery -> new DeliveryId(delivery.getId())).toList(),
                this.name
        );
    }


}
