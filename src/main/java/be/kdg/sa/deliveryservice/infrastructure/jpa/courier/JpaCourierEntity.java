package be.kdg.sa.deliveryservice.infrastructure.jpa.courier;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.infrastructure.jpa.delivery.JpaDeliveryEntity;
import be.kdg.sa.deliveryservice.infrastructure.jpa.delivery.JpaDeliveryRepository;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Couriers")
public class JpaCourierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_delivery_id")
    private JpaDeliveryEntity currentDeliveryId;

    @OneToMany(mappedBy = "courier", fetch = FetchType.LAZY)
    private List<JpaDeliveryEntity> pastDeliveries;

    protected JpaCourierEntity() {}

    public JpaCourierEntity(UUID id) {
        this.id = id;;
    }

    public UUID getId() {
        return id;
    }

    public static JpaCourierEntity fromDomain(Courier courier) {
        JpaCourierEntity jpaCourierEntity = new JpaCourierEntity(courier.getId().id());

        if (courier.getOrderId() != null) {
            JpaDeliveryEntity currentDelivery = JpaDeliveryRepository.findById(courier.getOrderId().id())
                                                                     .orElse(null); // or throw an exception if delivery must exist
            jpaCourierEntity.currentDeliveryId = currentDelivery;
        }
        return jpaCourierEntity;
    }



}
