package be.kdg.sa.deliveryservice.infrastructure.courier;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierEntity;
import be.kdg.sa.deliveryservice.infrastructure.courier.jpa.JpaCourierRepository;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryEntity;
import be.kdg.sa.deliveryservice.infrastructure.delivery.jpa.JpaDeliveryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbCourierRepositoryTest {

    private static final OrderId orderId = OrderId.create();
    private static final CourierId courierId = CourierId.create();
    private static final String courierName = "John";
    private static final DeliveryId deliveryId = DeliveryId.create();

    @Mock
    private JpaDeliveryRepository jpaDeliveryRepository;

    @Mock
    private JpaCourierRepository jpaCourierRepository;

    @InjectMocks
    private DbCourierRepository sut;

    @Test
    void findByIdReturnsCourierWhenFound() {
        // arrange
        Courier courier = new Courier(courierId, courierName);
        JpaCourierEntity jpaCourierEntity = JpaCourierEntity.fromDomain(courier, null, List.of());

        given(jpaCourierRepository.findById(courierId.id())).willReturn(Optional.of(jpaCourierEntity));
        // act
        var resultOptional = sut.findById(courierId);
        // assert
        assertThat(resultOptional).isPresent();
        var result = resultOptional.get();
        assertCompareCouriers(result, courier);
    }

    @Test
    void findByIdReturnsEmptyWhenCourierNotFound() {
        // arrange
        given(jpaCourierRepository.findById(courierId.id())).willReturn(Optional.empty());
        // act
        var resultOptional = sut.findById(courierId);
        // assert
        assertThat(resultOptional).isNotPresent();
    }

    @Test
    void saveShouldSaveCourierWithoutAnyDeliveries() {
        // arrange
        Courier courier = new Courier(courierId, courierName);

        ArgumentCaptor<JpaCourierEntity> entityCaptor = ArgumentCaptor.forClass(JpaCourierEntity.class);

        // act
        sut.save(courier);

        // assert
        verify(jpaCourierRepository).save(entityCaptor.capture());
        Courier capturedEntity = entityCaptor.getValue().toDomain();
        assertCompareCouriers(capturedEntity, courier);
        verify(jpaDeliveryRepository, never()).findById(any()); // once
    }

    @Test
    void saveShouldSaveCourierWithCurrentDelivery() {
        // arrange
        Delivery delivery = new Delivery(deliveryId, orderId);
        Courier courier = new Courier(courierId, courierName);
        delivery.claim(courierId); // not sure if needed
        courier.claim(deliveryId);

        JpaDeliveryEntity jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        JpaCourierEntity jpaCourierEntity = JpaCourierEntity.fromDomain(courier, jpaDeliveryEntity, List.of());
        jpaDeliveryEntity.setCourier(jpaCourierEntity);

        ArgumentCaptor<JpaCourierEntity> entityCaptor = ArgumentCaptor.forClass(JpaCourierEntity.class);

        given(jpaDeliveryRepository.findById(deliveryId.id())).willReturn(Optional.of(jpaDeliveryEntity));

        // act
        sut.save(courier);

        // assert
        verify(jpaCourierRepository).save(entityCaptor.capture());
        Courier capturedEntity = entityCaptor.getValue().toDomain();
        assertCompareCouriers(capturedEntity, courier);
        verify(jpaDeliveryRepository).findById(deliveryId.id()); // once
    }

    @Test
    void saveShouldSaveCourierWithPastDelivery() {
        // arrange
        Delivery delivery = new Delivery(deliveryId, orderId);
        Courier courier = new Courier(courierId, courierName);
        delivery.claim(courierId); // not sure if needed
        courier.addPastDelivery(deliveryId);

        JpaDeliveryEntity jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        JpaCourierEntity jpaCourierEntity = JpaCourierEntity.fromDomain(courier, null, List.of(jpaDeliveryEntity) );
        jpaDeliveryEntity.setCourier(jpaCourierEntity);

        ArgumentCaptor<JpaCourierEntity> entityCaptor = ArgumentCaptor.forClass(JpaCourierEntity.class);

        given(jpaDeliveryRepository.findById(deliveryId.id())).willReturn(Optional.of(jpaDeliveryEntity));

        // act
        sut.save(courier);

        // assert
        verify(jpaCourierRepository).save(entityCaptor.capture());
        Courier capturedEntity = entityCaptor.getValue().toDomain();
        assertCompareCouriers(capturedEntity, courier);
        verify(jpaDeliveryRepository).findById(deliveryId.id()); // once
    }

    private void assertCompareCouriers(Courier thisCourier, Courier otherCourier) {
        assertThat(thisCourier.getId()).isEqualTo(otherCourier.getId());
        assertThat(thisCourier.getName()).isEqualTo(otherCourier.getName());
        assertThat(thisCourier.getCurrentDeliveryId()).isEqualTo(otherCourier.getCurrentDeliveryId());
        assertThat(thisCourier.getPastDeliveries()).isEqualTo(otherCourier.getPastDeliveries());
    }
}