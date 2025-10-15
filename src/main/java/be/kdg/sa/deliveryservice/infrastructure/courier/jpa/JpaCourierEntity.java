package be.kdg.sa.deliveryservice.infrastructure.courier.jpa;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Couriers")
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

    protected JpaCourierEntity() {}

    public JpaCourierEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static JpaCourierEntity fromDomain(Courier courier, JpaDeliveryEntity currentDelivery, List<JpaDeliveryEntity> pastDeliveries) {
        JpaCourierEntity jpaCourierEntity = new JpaCourierEntity(courier.getId().id(), courier.getName());
        jpaCourierEntity.currentdelivery = currentDelivery;
        jpaCourierEntity.pastDeliveries = pastDeliveries;
        return jpaCourierEntity;
    }

    public Courier toDomain() {
        Courier courier = new Courier(new CourierId(this.id), this.name);
        if (this.currentdelivery != null) {
            courier.claim(new DeliveryId(this.currentdelivery.getId()));
        }
        this.pastDeliveries.forEach(delivery ->
                courier.addPastDelivery(new DeliveryId(delivery.getId()))
        );
        return courier;
    }


}
