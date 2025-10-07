package be.kdg.sa.deliveryservice.infrastructure.courier.jpa;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryRepository;
import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;
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

    @Column
    private String name;

    protected JpaCourierEntity() {}

    public JpaCourierEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public static JpaCourierEntity fromDomain(Courier courier, JpaDeliveryRepository deliveryRepository) {
        JpaCourierEntity jpaCourierEntity = new JpaCourierEntity(courier.getId().id(), courier.getName());

        if (courier.getCurrentDeliveryId() != null) {
            // or throw an exception if delivery must exist
            jpaCourierEntity.currentDeliveryId = deliveryRepository.findById(courier.getCurrentDeliveryId().id())
                    .orElse(null);


        }

        jpaCourierEntity.pastDeliveries = courier.getPastDeliveries().stream()
                .map(c -> deliveryRepository.findById(c.id()))
//               the flatmap is the same as below. Using Optional.stream() which returns either empty stream or single-element stream
//                  .filter(Optional::isPresent)
//                  .map(Optional::get)
                .flatMap(Optional::stream)
                .toList();

        return jpaCourierEntity;
    }

    public Courier toDomain() {
        Courier courier = new Courier(new CourierId(this.id), this.name);
        if (this.currentDeliveryId != null) {
            courier.claim(new DeliveryId(this.currentDeliveryId.getId()));
        }
        this.pastDeliveries.forEach(delivery ->
                courier.addPastDelivery(new DeliveryId(delivery.getId()))
        );
        return courier;
    }


}
