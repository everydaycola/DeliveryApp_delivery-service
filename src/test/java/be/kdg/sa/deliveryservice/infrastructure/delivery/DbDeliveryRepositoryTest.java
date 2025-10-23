package be.kdg.sa.deliveryservice.infrastructure.delivery;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DbDeliveryRepositoryTest {

    static double basePayout=3.0;
    static double perMinutePayout=0.3;
    static double minimumMinutes=5.0;
    static double maximumMinutes=30.0;

    private static final OrderId orderId = OrderId.create();
    private static final OrderId orderId2 = OrderId.create();
    private static final CourierId courierId = CourierId.create();
    private static final DeliveryId deliveryId = DeliveryId.create();
    private static final DeliveryId deliveryId2 = DeliveryId.create();

    @Mock
    private JpaDeliveryRepository jpaDeliveryRepository;

    @Mock
    private JpaCourierRepository jpaCourierRepository;

    @InjectMocks
    private DbDeliveryRepository sut;

    @Test
    void findAllByStatusReturnsAllDeliveriesWithStatus() {
        // arrange
        Delivery delivery = new Delivery(deliveryId, orderId);
        Delivery delivery2 = new Delivery(deliveryId2, orderId2);
        JpaDeliveryEntity jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        JpaDeliveryEntity jpaDeliveryEntity2 = JpaDeliveryEntity.fromDomain(delivery2, null);

        given(jpaDeliveryRepository.findAllByStatus("UNCLAIMED")).willReturn(Optional.of(List.of(jpaDeliveryEntity, jpaDeliveryEntity2)));

        // act
        var result = sut.findAllByStatus(DeliveryStatus.UNCLAIMED);

        // assert
        assertThat(result).hasSize(2);
        assertCompareDeliveries(result.get(0), delivery);
        assertCompareDeliveries(result.get(1), delivery2);
        verify(jpaDeliveryRepository).findAllByStatus("UNCLAIMED");
    }

    @Test
    void findAllByStatusReturnsEmptyWhenNoDeliveriesWithStatus() {
        // arrange
        given(jpaDeliveryRepository.findAllByStatus("CLAIMED")).willReturn(Optional.empty());

        // act
        var result = sut.findAllByStatus(DeliveryStatus.CLAIMED);

        // assert
        assertThat(result).isEmpty();
        verify(jpaDeliveryRepository).findAllByStatus("CLAIMED");
    }

    @Test
    void findByIdReturnsDeliveryWhenFound() {
        // arrange
        Delivery delivery = new Delivery(deliveryId, orderId);
        JpaDeliveryEntity jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);

        given(jpaDeliveryRepository.findById(deliveryId.id())).willReturn(Optional.of(jpaDeliveryEntity));
        // act
        var result = sut.findById(deliveryId);
        // assert
        assertThat(result).isPresent();
        assertCompareDeliveries(result.get(), delivery);
    }

    @Test
    void findByIdReturnsEmptyWhenDeliveryNotFound() {
        // arrange
        given(jpaDeliveryRepository.findById(deliveryId.id())).willReturn(Optional.empty());
        // act
        var result = sut.findById(deliveryId);
        // assert
        assertThat(result).isNotPresent();
    }

    @Test
    void findCompletedDeliveriesForReturnsCompletedDeliveriesForCourier() {
        // arrange
        Courier courier = new Courier(courierId, "John");
        Delivery delivery = new Delivery(deliveryId, orderId);
        Delivery delivery2 = new Delivery(deliveryId2, orderId2);
        delivery.claim(courierId);
        delivery2.claim(courierId);
        delivery.setReadyNow();
        delivery2.setReadyNow();
        delivery.pickUp();
        delivery2.pickUp();
        delivery.finishNow(basePayout, perMinutePayout, minimumMinutes, maximumMinutes);
        delivery2.finishNow(basePayout, perMinutePayout, minimumMinutes, maximumMinutes);
        JpaDeliveryEntity jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        JpaDeliveryEntity jpaDeliveryEntity2 = JpaDeliveryEntity.fromDomain(delivery2, null);
        JpaCourierEntity jpaCourierEntity = JpaCourierEntity.fromDomain(courier, null, List.of(jpaDeliveryEntity, jpaDeliveryEntity2));
        jpaDeliveryEntity.setCourier(jpaCourierEntity);
        jpaDeliveryEntity2.setCourier(jpaCourierEntity);

        given(jpaDeliveryRepository.findAllByCourierIdAndStatus(courierId.id(), "DELIVERED")).willReturn(Optional.of(List.of(jpaDeliveryEntity, jpaDeliveryEntity2)));

        // act
        var result = sut.findCompletedDeliveriesFor(courierId);

        // assert
        assertThat(result).hasSize(2);
        assertCompareDeliveries(result.get(0), delivery);
        assertCompareDeliveries(result.get(1), delivery2);
        verify(jpaDeliveryRepository).findAllByCourierIdAndStatus(courierId.id(), "DELIVERED");
    }

    @Test
    void findCompletedDeliveriesForReturnsEmptyListWhenCourierNotFound() {
        // arrange
        given(jpaDeliveryRepository.findAllByCourierIdAndStatus(courierId.id(), "DELIVERED")).willReturn(Optional.empty());

        // act
        var result = sut.findCompletedDeliveriesFor(courierId);

        // assert
        assertThat(result).isEmpty();
        verify(jpaDeliveryRepository).findAllByCourierIdAndStatus(courierId.id(), "DELIVERED");
    }

    @Test
    void findCompletedDeliveriesForReturnsEmptyListWhenNoDeliveriesForCourier() {
        // arrange
        given(jpaDeliveryRepository.findAllByCourierIdAndStatus(courierId.id(), "DELIVERED")).willReturn(Optional.of(List.of()));

        // act
        var result = sut.findCompletedDeliveriesFor(courierId);

        // assert
        assertThat(result).isEmpty();
        verify(jpaDeliveryRepository).findAllByCourierIdAndStatus(courierId.id(), "DELIVERED");
    }

    @Test
    void saveShouldSaveDeliveryWithoutCourier() {
        // arrange
        Delivery delivery = new Delivery(deliveryId, orderId);

        ArgumentCaptor<JpaDeliveryEntity> entityCaptor = ArgumentCaptor.forClass(JpaDeliveryEntity.class);

        // act
        sut.save(delivery);

        // assert

        verify(jpaDeliveryRepository).save(entityCaptor.capture());
        Delivery capturedEntity = entityCaptor.getValue().toDomain();
        assertCompareDeliveries(capturedEntity, delivery);
    }

    @Test
    void saveShouldSaveDeliveryWithCourier() {
        // arrange
        Delivery delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        Courier courier = new Courier(courierId, "John");

        JpaDeliveryEntity jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        JpaCourierEntity jpaCourierEntity = JpaCourierEntity.fromDomain(courier, jpaDeliveryEntity, null);
//        jpaDeliveryEntity.setCourier(jpaCourierEntity); // not needed in this test

        given(jpaCourierRepository.findById(courierId.id())).willReturn(Optional.of(jpaCourierEntity));

        ArgumentCaptor<JpaDeliveryEntity> entityCaptor = ArgumentCaptor.forClass(JpaDeliveryEntity.class);

        // act
        sut.save(delivery);

        // assert
        verify(jpaDeliveryRepository).save(entityCaptor.capture());
        Delivery capturedEntity = entityCaptor.getValue().toDomain();
        assertCompareDeliveries(capturedEntity, delivery);
    }

    private void assertCompareDeliveries(Delivery thisDelivery, Delivery otherDelivery) {
        assertThat(thisDelivery.getId()).isEqualTo(otherDelivery.getId());
        assertThat(thisDelivery.getOrderId()).isEqualTo(otherDelivery.getOrderId());
        assertThat(thisDelivery.getCourierId()).isEqualTo(otherDelivery.getCourierId());
        assertThat(thisDelivery.getStatus()).isEqualTo(otherDelivery.getStatus());
        assertThat(thisDelivery.getStartTime()).isEqualTo(otherDelivery.getStartTime());
        assertThat(thisDelivery.getEndTime()).isEqualTo(otherDelivery.getEndTime());
        assertThat(thisDelivery.getPayout()).isEqualTo(otherDelivery.getPayout());
    }
}