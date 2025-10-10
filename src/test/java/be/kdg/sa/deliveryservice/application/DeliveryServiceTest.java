package be.kdg.sa.deliveryservice.application;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService sut;

    @Test
    void FindAllShouldReturnListOfDeliveries() {
        // arrange
        DeliveryId deliveryId1 = DeliveryId.create();
        DeliveryId deliveryId2 = DeliveryId.create();
        OrderId orderId1 = OrderId.create();
        OrderId orderId2 = OrderId.create();

        Delivery delivery1 = new Delivery(deliveryId1, orderId1);
        Delivery delivery2 = new Delivery(deliveryId2, orderId2);

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);

        given(deliveryRepository.findAll()).willReturn(deliveries);

        // act
        var result = sut.findAll();

        // assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(delivery1);
        assertThat(result.get(1)).isEqualTo(delivery2);
        verify(deliveryRepository).findAll();
    }

    @Test
    void findCourierByIdShouldReturnCourier() {
        // arrange
        CourierId courierId = CourierId.create();
        Courier courier = new Courier(courierId, "John");

        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));

        // act
        var result = sut.findCourierById(courierId);

        // assert
        assertThat(result).isEqualTo(courier);
        verify(courierRepository).findById(courierId);
    }

    @Test
    void findCourierByIdShouldThrowExceptionWhenCourierNotFound() {
        // arrange
        CourierId courierId = CourierId.create();

        given(courierRepository.findById(courierId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.findCourierById(courierId));

        verify(courierRepository).findById(courierId);
    }

    @Test
    void claimShouldClaimDeliveryAndSaveBothAndReturnDelivery() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, OrderId.create());
        Courier courier = new Courier(courierId, "John");

        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));
        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        var result = sut.claim(deliveryId, courierId);

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
        DeliveryId deliveryId = DeliveryId.create();
        CourierId courierId = CourierId.create();
        Courier courier = new Courier(courierId, "John");

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
        DeliveryId deliveryId = DeliveryId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, OrderId.create());

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
        DeliveryId deliveryId1 = DeliveryId.create();
        DeliveryId deliveryId2 = DeliveryId.create();
        OrderId orderId1 = OrderId.create();
        OrderId orderId2 = OrderId.create();
        CourierId courierId = CourierId.create();

        Delivery delivery1 = new Delivery(deliveryId1, orderId1);
        Delivery delivery2 = new Delivery(deliveryId2, orderId2);

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);

        given(deliveryRepository.findDeliveriesFor(courierId)).willReturn(deliveries);

        // act
        var result = sut.findCompletedDeliveries(courierId);

        // assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(delivery1);
        assertThat(result.get(1)).isEqualTo(delivery2);
    }

    @Test
    void findAllUnclaimedShouldReturnListOfDeliveries() {
        // arrange
        DeliveryId deliveryId1 = DeliveryId.create();
        DeliveryId deliveryId2 = DeliveryId.create();
        OrderId orderId1 = OrderId.create();
        OrderId orderId2 = OrderId.create();

        Delivery delivery1 = new Delivery(deliveryId1, orderId1);
        Delivery delivery2 = new Delivery(deliveryId2, orderId2);

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);

        given(deliveryRepository.findAllByStatus(DeliveryStatus.UNCLAIMED)).willReturn(deliveries);

        // act
        var result = sut.findAllUnclaimed();

        // assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(delivery1);
        assertThat(result.get(1)).isEqualTo(delivery2);
    }

    @Test
    void unClaimShouldUnClaimBothAndSaveBothAndReturnCourier() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, OrderId.create());
        Courier courier = new Courier(courierId, "John");

        delivery.claim(courierId);
        courier.claim(deliveryId);

        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));
        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        var result = sut.unClaim(courierId);

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
        DeliveryId deliveryId = DeliveryId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, OrderId.create());

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
        DeliveryId deliveryId = DeliveryId.create();
        CourierId courierId = CourierId.create();
        Courier courier = new Courier(courierId, "John");

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
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        var result = sut.ready(deliveryId);

        // assert
        assertThat(result).isEqualTo(delivery);

        verify(deliveryRepository).save(delivery);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.READY_FOR_PICKUP);
        assertThat(result.getStartTime()).isNotNull();
    }

    @Test
    void readyShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.ready(deliveryId));

        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void pickupShouldSetDeliveryToReadyAndSaveDeliveryAndReturnDelivery() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        var result = sut.pickup(deliveryId);

        // assert
        assertThat(result).isEqualTo(delivery);

        verify(deliveryRepository).save(delivery);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.IN_DELIVERY);
    }

    @Test
    void pickupShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.pickup(deliveryId));

        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void deliverShouldSetDeliveryToReadyAndSaveDeliveryAndReturnDelivery() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, orderId);
        Courier courier = new Courier(courierId, "John");
        delivery.claim(courierId);
        courier.claim(deliveryId);
        delivery.setReadyNow();
        delivery.pickUp();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));
        given(courierRepository.findById(courierId)).willReturn(Optional.of(courier));

        // act
        var result = sut.deliver(deliveryId);

        // assert
        assertThat(result).isEqualTo(delivery);

        verify(deliveryRepository).save(delivery);
        verify(courierRepository).save(courier);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(result.getEndTime()).isNotNull();
        assertThat(result.getPayout()).isNotEqualTo(0);
    }

    @Test
    void deliverShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();
        delivery.pickUp();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.deliver(deliveryId));

        verify(deliveryRepository, never()).save(delivery);

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_DELIVERY);
        assertThat(delivery.getEndTime()).isNull();
        assertThat(delivery.getPayout()).isEqualTo(0);
    }

    @Test
    void deliverShouldThrowExceptionWhenCourierNotFound() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();
        CourierId courierId = CourierId.create();
        Delivery delivery = new Delivery(deliveryId, orderId);
        delivery.claim(courierId);
        delivery.setReadyNow();
        delivery.pickUp();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));
        given(courierRepository.findById(courierId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.deliver(deliveryId));

        verify(deliveryRepository, never()).save(delivery);

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_DELIVERY);
        assertThat(delivery.getEndTime()).isNull();
        assertThat(delivery.getPayout()).isEqualTo(0);
    }

    @Test
    void findDeliveryShouldReturnDelivery() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();
        Delivery delivery = new Delivery(deliveryId, orderId);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // act
        var result = sut.findDelivery(deliveryId);

        // assert
        assertThat(result).isEqualTo(delivery);
    }

    @Test
    void findDeliveryShouldThrowExceptionWhenDeliveryNotFound() {
        // arrange
        DeliveryId deliveryId = DeliveryId.create();
        OrderId orderId = OrderId.create();

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class,
                () -> sut.findDelivery(deliveryId));
    }
}