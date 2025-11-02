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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DbDeliveryRepositoryTest {

    static final double basePayout=3.0;
    static final double perMinutePayout=0.3;
    static final double minimumMinutes=5.0;
    static final double maximumMinutes=30.0;

    private static final OrderId orderId = OrderId.create();
    private static final OrderId orderId2 = OrderId.create();
    private static final CourierId courierId = CourierId.create();
    private static final DeliveryId deliveryId = DeliveryId.create();
    private static final DeliveryId deliveryId2 = DeliveryId.create();

    private static final LocalDateTime startTime = LocalDateTime.now();
    private static final LocalDateTime endTime = startTime.plusMinutes(10);

    @Mock
    private JpaDeliveryRepository jpaDeliveryRepository;

    @Mock
    private JpaCourierRepository jpaCourierRepository;

    @InjectMocks
    private DbDeliveryRepository sut;

    @Test
    void findDeliveryByOrderIdShouldReturnFoundAsOptional() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        final var jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        given(jpaDeliveryRepository.findByOrderId(orderId.id())).willReturn(Optional.of(jpaDeliveryEntity));
        // act
        final var result = sut.findDeliveryByOrderId(orderId);
        // assert
        assertThat(result).isPresent();
        assertCompareDeliveries(result.get(), delivery);
    }

    @Test
    void findDeliveryByOrderIdShouldReturnEmptyOptionalWhenNotFound() {
        // arrange
        given(jpaDeliveryRepository.findByOrderId(orderId.id())).willReturn(Optional.empty());
        // act
        final var result = sut.findDeliveryByOrderId(orderId);
        // assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAllByStatusReturnsAllDeliveriesWithStatus() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        final var delivery2 = new Delivery(deliveryId2, orderId2);
        final var jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        final var jpaDeliveryEntity2 = JpaDeliveryEntity.fromDomain(delivery2, null);

        given(jpaDeliveryRepository.findAllByStatus("UNCLAIMED")).willReturn(Optional.of(List.of(jpaDeliveryEntity, jpaDeliveryEntity2)));

        // act
        final var result = sut.findAllByStatus(DeliveryStatus.UNCLAIMED);

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
        final var result = sut.findAllByStatus(DeliveryStatus.CLAIMED);

        // assert
        assertThat(result).isEmpty();
        verify(jpaDeliveryRepository).findAllByStatus("CLAIMED");
    }

    @Test
    void findByIdReturnsDeliveryWhenFound() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        final var jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);

        given(jpaDeliveryRepository.findById(deliveryId.id())).willReturn(Optional.of(jpaDeliveryEntity));
        // act
        final var result = sut.findById(deliveryId);
        // assert
        assertThat(result).isPresent();
        assertCompareDeliveries(result.get(), delivery);
    }

    @Test
    void findByIdReturnsEmptyWhenDeliveryNotFound() {
        // arrange
        given(jpaDeliveryRepository.findById(deliveryId.id())).willReturn(Optional.empty());
        // act
        final var result = sut.findById(deliveryId);
        // assert
        assertThat(result).isNotPresent();
    }

    @Test
    void findCompletedDeliveriesForReturnsCompletedDeliveriesForCourier() {
        // arrange
        final var courier = new Courier(courierId, "John");
        final var delivery = new Delivery(deliveryId, orderId);
        final var delivery2 = new Delivery(deliveryId2, orderId2);
        delivery.claim(courierId);
        delivery2.claim(courierId);
        delivery.setReadyNow();
        delivery2.setReadyNow();
        delivery.pickUp();
        delivery2.pickUp();
        delivery.finishNow(basePayout, perMinutePayout, minimumMinutes, maximumMinutes);
        delivery2.finishNow(basePayout, perMinutePayout, minimumMinutes, maximumMinutes);
        final var jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        final var jpaDeliveryEntity2 = JpaDeliveryEntity.fromDomain(delivery2, null);
        final var jpaCourierEntity = JpaCourierEntity.fromDomain(courier, null, List.of(jpaDeliveryEntity, jpaDeliveryEntity2));
        jpaDeliveryEntity.setCourier(jpaCourierEntity);
        jpaDeliveryEntity2.setCourier(jpaCourierEntity);

        given(jpaDeliveryRepository.findAllByCourierIdAndStatus(courierId.id(), "DELIVERED")).willReturn(Optional.of(List.of(jpaDeliveryEntity, jpaDeliveryEntity2)));

        // act
        final var result = sut.findCompletedDeliveriesFor(courierId);

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
        final var result = sut.findCompletedDeliveriesFor(courierId);

        // assert
        assertThat(result).isEmpty();
        verify(jpaDeliveryRepository).findAllByCourierIdAndStatus(courierId.id(), "DELIVERED");
    }

    @Test
    void findCompletedDeliveriesForReturnsEmptyListWhenNoDeliveriesForCourier() {
        // arrange
        given(jpaDeliveryRepository.findAllByCourierIdAndStatus(courierId.id(), "DELIVERED")).willReturn(Optional.of(List.of()));

        // act
        final var result = sut.findCompletedDeliveriesFor(courierId);

        // assert
        assertThat(result).isEmpty();
        verify(jpaDeliveryRepository).findAllByCourierIdAndStatus(courierId.id(), "DELIVERED");
    }

    @Test
    void findAllCompletedDeliveriesBetweenShouldReturnDeliveriesBetweenInterval() {
        // arrange
        final var courier = new Courier(courierId, "John");
        final var delivery = new Delivery(deliveryId, orderId, courierId, DeliveryStatus.DELIVERED, startTime, endTime, 3.5);
        final var delivery2 = new Delivery(deliveryId2, orderId2, courierId, DeliveryStatus.DELIVERED, startTime.plusMinutes(30), endTime.plusMinutes(30), 3.5);
        final var jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        final var jpaDeliveryEntity2 = JpaDeliveryEntity.fromDomain(delivery2, null);
        final var jpaCourierEntity = JpaCourierEntity.fromDomain(courier, null, List.of(jpaDeliveryEntity, jpaDeliveryEntity2));
        jpaDeliveryEntity.setCourier(jpaCourierEntity);
        jpaDeliveryEntity2.setCourier(jpaCourierEntity);

        given(jpaDeliveryRepository.findAllByStatusAndStartTimeIsAfterAndEndTimeIsBefore("DELIVERED", startTime.minusDays(20), endTime.plusDays(20))).willReturn(Optional.of(List.of(jpaDeliveryEntity, jpaDeliveryEntity2)));

        // act
        final var result = sut.findAllCompletedDeliveriesBetween(startTime.minusDays(20), endTime.plusDays(20));

        // assert
        assertThat(result).hasSize(2);
        assertCompareDeliveries(result.get(0), delivery);
        assertCompareDeliveries(result.get(1), delivery2);

    }

    @Test
    void findAllCompletedDeliveriesBetweenShouldReturnDeliveriesBetweenIntervalEvenWhenNoDeliveries() {
        // arrange
        given(jpaDeliveryRepository.findAllByStatusAndStartTimeIsAfterAndEndTimeIsBefore("DELIVERED", startTime.minusDays(10), endTime.plusDays(10))).willReturn(Optional.of(List.of()));

        // act
        final var result = sut.findAllCompletedDeliveriesBetween(startTime.minusDays(10), endTime.plusDays(10));

        // assert
        assertThat(result).isEmpty();

    }

    @Test
    void findAllCompletedDeliveriesBetweenShouldReturnDeliveriesBetweenIntervalEvenWhenNoResponse() {
        // arrange
        given(jpaDeliveryRepository.findAllByStatusAndStartTimeIsAfterAndEndTimeIsBefore("DELIVERED", startTime.minusDays(10), endTime.plusDays(10))).willReturn(Optional.empty());

        // act
        final var result = sut.findAllCompletedDeliveriesBetween(startTime.minusDays(10), endTime.plusDays(10));

        // assert
        assertThat(result).isEmpty();

    }

    @Test
    void saveShouldSaveDeliveryWithoutCourier() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);

        final var entityCaptor = ArgumentCaptor.forClass(JpaDeliveryEntity.class);

        // act
        sut.save(delivery);

        // assert

        verify(jpaDeliveryRepository).save(entityCaptor.capture());
        final var capturedEntity = entityCaptor.getValue().toDomain();
        assertCompareDeliveries(capturedEntity, delivery);
    }

    @Test
    void saveShouldSaveDeliveryWithCourier() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        final var courier = new Courier(courierId, "John");

        final var jpaDeliveryEntity = JpaDeliveryEntity.fromDomain(delivery, null);
        final var jpaCourierEntity = JpaCourierEntity.fromDomain(courier, jpaDeliveryEntity, null);
//        jpaDeliveryEntity.setCourier(jpaCourierEntity); // not needed in this test

        given(jpaCourierRepository.findById(courierId.id())).willReturn(Optional.of(jpaCourierEntity));

        final var entityCaptor = ArgumentCaptor.forClass(JpaDeliveryEntity.class);

        // act
        sut.save(delivery);

        // assert
        verify(jpaDeliveryRepository).save(entityCaptor.capture());
        final var capturedEntity = entityCaptor.getValue().toDomain();
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