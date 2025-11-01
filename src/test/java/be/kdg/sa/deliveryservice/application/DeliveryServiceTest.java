package be.kdg.sa.deliveryservice.application;

import be.kdg.sa.deliveryservice.config.DomainProperties;
import be.kdg.sa.deliveryservice.domain.NotFoundException;
import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierId;
import be.kdg.sa.deliveryservice.domain.courier.CourierRepository;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryId;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryStatus;
import be.kdg.sa.deliveryservice.domain.order.OrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    private static final OrderId orderId = OrderId.create();
    private static final OrderId orderId2 = OrderId.create();
    private static final CourierId courierId = CourierId.create();
    private static final CourierId courierId2 = CourierId.create();
    private static final DeliveryId deliveryId = DeliveryId.create();
    private static final DeliveryId deliveryId2 = DeliveryId.create();

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DomainProperties domainProperties;

    @InjectMocks
    private DeliveryService sut;

    @Test
    void findCourierByIdShouldReturnCourier() {
        // arrange
        final var courier = new Courier(courierId, "John");

        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));

        // act
        final var result = sut.findCourierById(courierId);

        // assert
        assertThat(result).isEqualTo(courier);
        verify(courierRepository).findById(courierId);
    }

    @Test
    void findCourierByIdShouldThrowExceptionWhenCourierNotFound() {
        // arrange
        given(courierRepository.findById(courierId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.findCourierById(courierId));

        verify(courierRepository).findById(courierId);
    }

    @Test
    void claimShouldClaimDeliveryAndSaveBothAndReturnDelivery() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        final var courier = new Courier(courierId, "John");

        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));
        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        final var result = sut.claim(deliveryId, courierId);

        // assert
        assertThat(result).isEqualTo(delivery);

        verify(courierRepository).save(courier);
        verify(deliveryRepository).save(delivery);

        assertThat(result.getCourierId()).isEqualTo(courierId);
        assertThat(courier.getCurrentDeliveryId()).isEqualTo(deliveryId);
    }

    @Test
    void claimShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        final var courier = new Courier(courierId, "John");

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                ()-> sut.claim(deliveryId, courierId));

        verify(courierRepository, never()).save(courier);

        assertThat(courier.getCurrentDeliveryId()).isNull();
    }

    @Test
    void claimShouldThrowExceptionWhenCourierNotFound() {
        // arrange
        final var delivery = new Delivery(deliveryId, OrderId.create());

        given(courierRepository.findById(courierId)).willReturn(Optional.empty());
        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.claim(deliveryId, courierId));

        verify(deliveryRepository, never()).save(delivery);

        assertThat(delivery.getCourierId()).isNull();
    }

    @Test
    void findCompletedDeliveries() {
        // arrange
        final var delivery1 = new Delivery(deliveryId, orderId);
        final var delivery2 = new Delivery(deliveryId2, orderId2);

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);

        given(deliveryRepository.findCompletedDeliveriesFor(courierId)).willReturn(deliveries);

        // act
        final var result = sut.findCompletedDeliveries(courierId);

        // assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(delivery1);
        assertThat(result.get(1)).isEqualTo(delivery2);
    }

    @Test
    void findAllUnclaimedShouldReturnListOfDeliveries() {
        // arrange
        final var delivery1 = new Delivery(deliveryId, orderId);
        final var delivery2 = new Delivery(deliveryId2, orderId2);

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);

        given(deliveryRepository.findAllByStatus(DeliveryStatus.UNCLAIMED)).willReturn(deliveries);

        // act
        final var result = sut.findAllUnclaimed();

        // assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(delivery1);
        assertThat(result.get(1)).isEqualTo(delivery2);
    }

    @Test
    void unClaimShouldUnClaimBothAndSaveBothAndReturnCourier() {
        // arrange
        final var delivery = new Delivery(deliveryId, OrderId.create());
        final var courier = new Courier(courierId, "John");

        delivery.claim(courierId);
        courier.claim(deliveryId);

        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));
        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        final var result = sut.unClaim(courierId);

        // assert
        assertThat(result).isEqualTo(courier);

        verify(courierRepository).save(courier);
        verify(deliveryRepository).save(delivery);

        assertThat(courier.getCurrentDeliveryId()).isNull();
        assertThat(delivery.getCourierId()).isNull();
    }

    @Test
    void unClaimShouldThrowExceptionWhenCourierNotFound() {
        // arrange
        final var delivery = new Delivery(deliveryId, OrderId.create());

        delivery.claim(courierId);

        given(courierRepository.findById(courierId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.unClaim(courierId));

        verify(deliveryRepository, never()).save(delivery);

        assertThat(delivery.getCourierId()).isEqualTo(courierId);
    }

    @Test
    void unClaimShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        final var courier = new Courier(courierId, "John");

        courier.claim(deliveryId);

        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));
        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.unClaim(courierId));

        verify(courierRepository, never()).save(courier);
    }

    @Test
    void readyShouldSetDeliveryToReadyAndSaveDeliveryAndReturnDelivery() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        final var result = sut.ready(deliveryId, courierId);

        // assert
        assertThat(result).isEqualTo(delivery);

        verify(deliveryRepository).save(delivery);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.READY_FOR_PICKUP);
        assertThat(result.getStartTime()).isNotNull();
    }

    @Test
    void readyShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.ready(deliveryId, courierId));

        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void readyShouldThrowExceptionWhenWrongCourier() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act & assert
        assertThrows(IllegalStateException.class,
                () -> sut.ready(deliveryId, courierId2));

        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void pickupShouldSetDeliveryToReadyAndSaveDeliveryAndReturnDelivery() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        final var result = sut.pickup(deliveryId, courierId);

        // assert
        assertThat(result).isEqualTo(delivery);

        verify(deliveryRepository).save(delivery);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.IN_DELIVERY);
    }

    @Test
    void pickupShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.pickup(deliveryId, courierId));

        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void pickupShouldThrowExceptionWhenWrongCourier() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act & assert
        assertThrows(IllegalStateException.class,
                () -> sut.pickup(deliveryId, courierId2));

        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void deliverShouldSetDeliveryToReadyAndSaveDeliveryAndReturnDelivery() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        final var courier = new Courier(courierId, "John");
        delivery.claim(courierId);
        courier.claim(deliveryId);
        delivery.setReadyNow();
        delivery.pickUp();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));
        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));
        given(domainProperties.getBasePayout()).willReturn(3.0);
        given(domainProperties.getPerMinutePayout()).willReturn(0.3);
        given(domainProperties.getMinimumMinutes()).willReturn(5.0);
        given(domainProperties.getMaximumMinutes()).willReturn(30.0);

        // act
        final var result = sut.deliver(deliveryId, courierId);

        // assert
        assertThat(result).isEqualTo(delivery);

        verify(deliveryRepository).save(delivery);
        verify(courierRepository).save(courier);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(result.getEndTime()).isNotNull();
        assertThat(result.getPayout()).isNotZero();
    }

    @Test
    void deliverShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();
        delivery.pickUp();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.deliver(deliveryId, courierId));

        verify(deliveryRepository, never()).save(delivery);

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_DELIVERY);
        assertThat(delivery.getEndTime()).isNull();
        assertThat(delivery.getPayout()).isZero();
    }

    @Test
    void deliverShouldThrowExceptionWhenCourierNotFound() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();
        delivery.pickUp();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));
        given(courierRepository.findById(courierId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.deliver(deliveryId, courierId2));

        verify(deliveryRepository, never()).save(delivery);

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_DELIVERY);
        assertThat(delivery.getEndTime()).isNull();
        assertThat(delivery.getPayout()).isZero();
    }

    @Test
    void deliverShouldThrowExceptionWhenWrongCourier() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        final var courier = new Courier(courierId, "John");
        delivery.claim(courierId);
        courier.claim(deliveryId);
        delivery.setReadyNow();
        delivery.pickUp();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));
        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));

        // act & assert
        assertThrows(IllegalStateException.class,
                () -> sut.deliver(deliveryId, courierId2));

        verify(deliveryRepository, never()).save(delivery);
        verify(courierRepository, never()).save(courier);

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_DELIVERY);
        assertThat(delivery.getEndTime()).isNull();
        assertThat(delivery.getPayout()).isZero();
    }

    @Test
    void findDeliveryShouldReturnDelivery() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        final var result = sut.findDelivery(deliveryId, courierId2);

        // assert
        assertThat(result).isEqualTo(delivery);
    }

    @Test
    void findDeliveryShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.findDelivery(deliveryId, courierId));
    }

    @Test
    void findDeliveryShouldThrowExceptionWhenWrongCourier() {
        // arrange
        final var delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act & assert
        assertThrows(IllegalStateException.class,
                () -> sut.findDelivery(deliveryId, courierId2));
    }
}